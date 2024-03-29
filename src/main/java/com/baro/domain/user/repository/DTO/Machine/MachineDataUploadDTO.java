package com.baro.domain.user.repository.DTO.Machine;

import com.baro.domain.user.repository.DTO.Machine.MachineBaseDTO;
import com.baro.domain.user.repository.DTO.Machine.MachineDataDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MachineDataUploadDTO {
    private MachineDataDTO machineData;
    private List<MachineBaseDTO> machineBaseList;
}
