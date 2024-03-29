package com.baro.domain.user.controller;

import com.baro.domain.cocktail.domain.Base;
import com.baro.domain.cocktail.service.BaseService;
import com.baro.domain.user.domain.Machine;
import com.baro.domain.user.repository.DAO.MachineBaseReadDAO;
import com.baro.domain.user.repository.DTO.Machine.MachineBaseDTO;
import com.baro.domain.user.repository.DTO.Machine.MachineBaseReadDTO;
import com.baro.domain.user.repository.DTO.Machine.MachineDataUploadDTO;
import com.baro.domain.user.service.MachineBaseService;
import com.baro.domain.user.service.MachineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/v1/machine")
@CrossOrigin(origins = {"http://localhost:3000", "http://15.165.86.77"}, allowedHeaders = {"Authorization", "Content-Type"})

@RequiredArgsConstructor
public class MachineController {
    private final MachineService machineService;
    private final BaseService baseService;
    private final MachineBaseService machineBaseService;

    @PostMapping("/data/read")
    public ResponseEntity machine_base_data_read_controller(@RequestBody MachineBaseReadDTO baseReadDTO){
        if(machineService.check_machine_id(baseReadDTO.getMachineId())){
            //아이디가 존재
           MachineBaseReadDAO machineBaseList = machineBaseService.read_machine_base_service(baseReadDTO.getMachineId());

           return ResponseEntity.ok(machineBaseList);
        }else{
            //아이디가 없음
            log.warn("존재하지 않은 머신아이디 시도");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("존재하지않는 머신아이디");
        }
    }

    @PostMapping("/data/upload")
    public ResponseEntity machine_data_upload_controller(@RequestBody MachineDataUploadDTO machineUploadData) {
        String machineId = machineUploadData.getMachineData().getMachineId();

        log.info("머신 베이스 등록 시작 ... {}",  machineId);

        if (!machineService.check_machine_id(machineId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("머신 아이디가 존재하지 않습니다. 관리자에게 문의하세요.");
        }

        if (!check_base_list(machineUploadData.getMachineBaseList())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("베이스 정보가 존재하지 않습니다. 관리자에게 문의하세요.");
        }

        if(!check_machine_base_line_number(machineUploadData.getMachineBaseList())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("베이스 리스트 조작이 잘못되었습니다. 관리자에게 문의하세요");
        }


        if(!machineService.check_machine_line_service(machineId , machineUploadData.getMachineBaseList().size())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("기계 라인보다 많은 베이스 등록을 시도하였습니다. 관리자에게 문의하세요.");
        }
        try {
            log.info("체크 성공.. 다음스텝으로 넘어갑니다.");
            /**
             * 이미 머신아이디에 대해 정보가 존재한다면 대체하는 기능
             * 라인이상으로 입력되면 거부하는 기능 check
             */
            Machine machine = machineService.find_machine_data_service(machineId);
            String machine_return_text;
            if(machineBaseService.already_exists_machineBase_check_service(machineId)){
                //존재하는 머신베이스
                log.info("이미 존재하는 머신베이스에 재업로드를 시작합니다.");
                machine_return_text = machineService.machine_data_reUpload_service(machine , machineUploadData.getMachineBaseList());
            }else{
                //존재하지 않음
                log.info("존재하지않은 기계 등록을 시작합니다.");
                machine_return_text = machineService.machine_data_upload_service(machine, machineUploadData.getMachineBaseList());
            }



            if ("success".equals(machine_return_text)) {
                return ResponseEntity.ok(machine_return_text);
            } else {
                log.warn("업로드 실패..  {}", machine_return_text);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(machine_return_text);
            }
        } catch (Exception e) {
            log.error("머신 데이터 업로드 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생. 관리자에게 문의하세요.");
        }
    }

    private boolean check_machine_base_line_number(List<MachineBaseDTO> machineBaseList){
        log.info("check_machine_base_line_number start");
        int checkFlag = machineBaseList.size();
        Set<Integer> uniqueLineNumbers = new HashSet<>();

        for (MachineBaseDTO machineBaseData : machineBaseList) {
            Integer baseLineNum = machineBaseData.getMachine_base_line();

            if (baseLineNum == null ||baseLineNum < 1 || baseLineNum > checkFlag || !uniqueLineNumbers.add(baseLineNum)) {
                log.error("Invalid or duplicate baseLineNum found: " + baseLineNum);
                return false;
            }
        }
        return true;

    }

    private boolean check_base_list(List<MachineBaseDTO> machineBaseList){
        log.info("machine check base List start");
        int checkFlag = machineBaseList.size();
        for(MachineBaseDTO baseData : machineBaseList){
            log.info("find base ... {}",baseData.getBase_seq());

            if(baseService.checkBaseToSeq(baseData.getBase_seq())){
                //존재
                checkFlag--;
            }else{
                //없음
                log.warn("base 정보가 없습니다.");
            }
        }

        if(checkFlag ==0){
            //확인됨
            return true;
        }else {
            return false;
        }

    }

}
