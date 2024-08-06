package ync.likelion.moguri_be.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ync.likelion.moguri_be.dto.*;
import ync.likelion.moguri_be.model.*;
import ync.likelion.moguri_be.repository.MoguriCodeRepository;
import ync.likelion.moguri_be.repository.MoguriRepository;
import ync.likelion.moguri_be.service.ItemService;
import ync.likelion.moguri_be.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/moguri")
public class MoguriController {

    private final UserService userService;
    private final MoguriRepository moguriRepository;
    private final MoguriCodeRepository moguriCodeRepository;

    private final ItemService itemService;
    @Autowired
    public MoguriController(UserService userService, MoguriRepository moguriRepository, MoguriCodeRepository moguriCodeRepository, ItemService itemService) {
        this.userService = userService;
        this.moguriRepository = moguriRepository;
        this.moguriCodeRepository = moguriCodeRepository;
        this.itemService = itemService;
    }

    @Operation(summary = "모구리 생성", description = "사용자가 새로운 모구리를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "모구리 생성 성공"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 모구리 이름입니다."),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다.")
    })
    @PostMapping
    public ResponseEntity<Object> createMoguri(
            @Parameter(description = "Authorization 헤더", required = true) @RequestHeader("Authorization") String authorization,
            @RequestBody CreateMoguriRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // 토큰에서 사용자 이름 추출
        MoguriDto moguriDto = request.getMoguri();
//        System.out.println(username);
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        // 사용자의 모구리 객체 가져오기
        Moguri existingMoguri = moguriRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("사용자와 관련된 모구리를 찾을 수 없습니다."));

        // 해당 모구리에서 모구리 코드 가져오기
        MoguriCode moguriCode = existingMoguri.getMoguriCode();
        // 모구리 이름 중복 체크
        if (moguriDto.getName() != null && !moguriDto.getName().isEmpty()) {
            if (moguriRepository.existsByName(moguriDto.getName())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("이미 존재하는 모구리 이름입니다."));
            }
        }
        System.out.println(moguriDto.getTargetWeight());
        System.out.println(moguriDto.getName());
        // UserDto 설정
        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        userDto.setEmail(user.getEmail()); // 기존 이메일 설정
        userDto.setTargetWeight(moguriDto.getTargetWeight());
        userService.save(userDto); // 사용자 정보를 저장

        existingMoguri.setMoguriCode(moguriCode);
        existingMoguri.setName(moguriDto.getName());

        moguriRepository.save(existingMoguri); // 모구리 저장

        // MoguriResponse 생성
        MoguriResponse response = new MoguriResponse();
        response.setMessage("Create moguri successful");
        response.setMoguri(moguriDto); // 필요시 생성된 모구리의 정보를 포함하도록 수정 가능

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "오늘의 몸무게 입력 및 모구리 진화", description = "사용자가 오늘의 몸무게를 입력하고 목표 몸무게에 따라 모구리를 진화시킵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "몸무게 저장 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 몸무게 값)"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (유효하지 않은 또는 만료된 토큰)"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/grow")
    @Transactional
    public ResponseEntity<Object> growMoguri(
            @Parameter(description = "Authorization 헤더", required = true) @RequestHeader("Authorization") String authorization,
            @RequestBody WeightDto weightDto) {

        // 유효성 검사
        if (weightDto.getWeight() <= 0) {
            return ResponseEntity.badRequest().body("유효하지 않은 몸무게 값입니다.");
        }

        boolean isEvolved;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        float targetWeight = user.getTargetWeight();
        String weightChangeMessage;

        float weightDifference = weightDto.getWeight() - targetWeight;
        Moguri moguri = moguriRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("현재 모구리를 찾을 수 없습니다."));

        if (weightDifference <= 0) {
            weightChangeMessage = "축하합니다! 목표에 도달하셨네요!";
            isEvolved = true;

        } else {
            weightChangeMessage = "목표까지 " + weightDifference + "kg 남았어요!";
            isEvolved = false;
        }
        // 모구리 코드 진화
        MoguriCode evolvedMoguriCode = getEvolvedMoguriCode(moguri.getMoguriCode(), isEvolved);
        if (evolvedMoguriCode == null) {
            throw new RuntimeException("진화된 모구리 코드가 존재하지 않습니다.");
        }
        moguri.setMoguriCode(evolvedMoguriCode); // 모구리 코드 설정
        moguriRepository.save(moguri); // 모구리 저장
        return ResponseEntity.status(HttpStatus.CREATED).body(createGrowResponse(weightChangeMessage, isEvolved, user, moguri));
    }


    private GrowMoguriResponse createGrowResponse(String targetDifference, boolean isEvolved, User user, Moguri moguri) {
        // MoguriCode ID를 사용하여 MoguriCode 객체를 조회
        MoguriCode moguriCode = moguriCodeRepository.findById(moguri.getMoguriCode().getId())
                .orElseThrow(() -> new RuntimeException("MoguriCode를 찾을 수 없습니다."));

        MoguriGrowDetailDto moguriGrowDetailDto = new MoguriGrowDetailDto(
                moguri.getMoguriCode().getId(),
                moguriCode.getImageUrl(), // 이미지 URL을 가져옴
                user.getTargetWeight() // 사용자에서 목표 몸무게 가져오기
        );

        return new GrowMoguriResponse(
                "몸무게가 성공적으로 입력되었습니다.",
                targetDifference,
                isEvolved,
                moguriGrowDetailDto // 새로운 DTO 사용
        );
    }


    private MoguriCode getEvolvedMoguriCode(MoguriCode currentMoguriCode, Boolean isEvolved ) {
        if (currentMoguriCode == null) {
            throw new IllegalArgumentException("현재 모구리 코드가 null입니다."); // null 체크
        }

        int currentId = currentMoguriCode.getId();
        int newId;
        System.out.println(currentId);
        // 진화 로직: ID에 따라 새로운 ID 결정
        if (isEvolved) {
            switch (currentId) {
                case 1 -> newId = 2; // 모구리 코드 1에서 2로 진화
                case 3 -> newId = 4;
                case 5 -> newId = 6;
                case 7 -> newId = 8;
                case 9 -> newId = 10;
                case 11 -> newId = 12;
                default -> {
                    return currentMoguriCode; // 진화하지 않음
                }
            }
        } else{
            switch (currentId) {
                case 2 -> newId = 1; // 모구리 코드 1에서 2로 진화
                case 4 -> newId = 3;
                case 6 -> newId = 5;
                case 8 -> newId = 7;
                case 10 -> newId = 9;
                case 12 -> newId = 11;
                default -> {
                    return currentMoguriCode; // 진화하지 않음
                }
            }
        }

        return moguriCodeRepository.findById(newId)
                .orElseThrow(() -> new RuntimeException("진화된 모구리 코드가 존재하지 않습니다.")); // 데이터베이스에서 찾기
    }
    // 추가적인 메서드 (모구리 조회, 업데이트 등)도 여기에 추가할 수 있습니다.
    @Operation(summary = "사용자의 액세서리 목록 조회", description = "사용자의 액세서리 목록을 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "액세서리 조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/accessory")
    public ResponseEntity<Object> getUserAccessories(@RequestHeader("Authorization") String authorization) {
        return getUserItems(authorization, true);
    }
    @Operation(summary = "사용자의 배경화면 목록 조회", description = "사용자의 배경화면 목록을 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "배경화면 조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/background")
    public ResponseEntity<Object> getUserBackgrounds(@RequestHeader("Authorization") String authorization) {
        return getUserItems(authorization, false);
    }

    private ResponseEntity<Object> getUserItems(String authorization, boolean isAccessory) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (isAccessory) {
            List<AccessoryCode> userAccessories = itemService.getUserAccessoryItems(user);
            return ResponseEntity.ok().body(Map.of(
                    "message", "Accessory retrieved successfully",
                    "accessories", userAccessories.stream().map(accessory -> Map.of(
                            "id", accessory.getId(),
                            "name", accessory.getName(),
                            "imageUrl", accessory.getImageUrl()
                    )).collect(Collectors.toList())
            ));
        } else {
            List<BackgroundCode> userBackgrounds = itemService.getUserBackgroundItems(user);
            return ResponseEntity.ok().body(Map.of(
                    "message", "Background retrieved successfully",
                    "backgrounds", userBackgrounds.stream().map(background -> Map.of(
                            "id", background.getId(),
                            "name", background.getName(),
                            "imageUrl", background.getImageUrl()
                    )).collect(Collectors.toList())
            ));
        }
    }

    // 악세서리 착용/해제 엔드포인트
    // 악세서리 착용/해제 엔드포인트
    @Operation(summary = "액세서리 착용 또는 해제", description = "사용자가 액세서리를 착용하거나 해제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "액세서리 착용/해제 성공"),
            @ApiResponse(responseCode = "404", description = "요청한 아이템을 찾을 수 없음")
    })
    @PutMapping("/accessory")
    public ResponseEntity<Object> equipAccessory(@RequestBody AccessoryRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 현재 착용 중인 아이템 조회
        AccessoryCode currentAccessory = itemService.getCurrentEquippedAccessory(user);

        // 요청한 아이템 조회
        AccessoryCode requestedAccessory = itemService.getAccessoryById(request.getAccessory().getId());

        // 아이템이 존재하지 않는 경우
        if (requestedAccessory == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "요청한 아이템을 찾을 수 없습니다."));
        }

        // 이미 착용 중인 아이템을 다시 착용하는 경우
        if (currentAccessory != null && currentAccessory.getId() == requestedAccessory.getId()) {
            // 아이템 해제
            itemService.unequipAccessory(user, currentAccessory);
            return ResponseEntity.ok().body(Map.of(
                    "message", "Accessory unequipped successfully",
                    "accessory", Map.of(
                            "id", 0,
                            "name", "",
                            "imageUrl", ""
                    ),
                    "current_equipped_accessory", 0
            ));
        }

        // 현재 착용된 액세서리가 없는 경우
        if (currentAccessory == null || currentAccessory.getId() == 0) {
            itemService.equipAccessory(user, requestedAccessory);
            return ResponseEntity.ok().body(Map.of(
                    "message", "Accessory equipped successfully",
                    "accessory", Map.of(
                            "id", requestedAccessory.getId(),
                            "name", requestedAccessory.getName(),
                            "imageUrl", requestedAccessory.getImageUrl()
                    ),
                    "current_equipped_accessory", requestedAccessory.getId()
            ));
        }

        // 현재 착용된 액세서리가 있는 경우, 기존 액세서리를 해제하고 새로운 액세서리를 착용
        itemService.unequipAccessory(user, currentAccessory);
        itemService.equipAccessory(user, requestedAccessory);

        return ResponseEntity.ok().body(Map.of(
                "message", "Accessory equipped successfully",
                "accessory", Map.of(
                        "id", requestedAccessory.getId(),
                        "name", requestedAccessory.getName(),
                        "imageUrl", requestedAccessory.getImageUrl()
                ),
                "current_equipped_accessory", requestedAccessory.getId()
        ));
    }


    // 배경화면 설정/해제 엔드포인트
    @Operation(summary = "배경화면 설정 또는 해제", description = "사용자가 배경화면을 설정하거나 해제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "배경화면 설정/해제 성공"),
            @ApiResponse(responseCode = "404", description = "요청한 배경화면을 찾을 수 없음")
    })
    @PutMapping("/background")
    public ResponseEntity<Object> setBackground(@RequestBody BackgroundRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 요청한 배경화면 조회
        BackgroundCode requestedBackground = itemService.getBackgroundById(request.getBackground().getId());

        // 배경화면이 존재하지 않는 경우
        if (requestedBackground == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "요청한 배경화면을 찾을 수 없습니다."));
        }

        // 배경화면 설정 또는 해제 처리
        BackgroundCode resultBackground = itemService.toggleBackground(user, requestedBackground);

        if (resultBackground.getId() == 0) {
            // 해제된 경우
            return ResponseEntity.ok().body(Map.of(
                    "message", "Background unequipped successfully",
                    "background", Map.of(
                            "id", 0,
                            "name", "",
                            "imageUrl", ""
                    ),
                    "current_equipped_background", 0
            ));
        }

        // 설정된 경우
        return ResponseEntity.ok().body(Map.of(
                "message", "Background equipped successfully",
                "background", Map.of(
                        "id", resultBackground.getId(),
                        "name", resultBackground.getName(),
                        "imageUrl", resultBackground.getImageUrl()
                ),
                "current_eqipped_background", requestedBackground.getId()
        ));
    }
}
