package com.baro.domain.order.util;

import lombok.Getter;

@Getter
public enum GcodeMoveCoordinateData {
    FIRST(0, 1100 , 0),
    SECOND(800 ,1100 , 0),
    THIRD(1600 , 1100, 0),
    FORE(2350 , 1100, 0);

    private final int x;
    private final int y;
    private final int z;
    GcodeMoveCoordinateData(int x , int y , int z){
        this.x = x;
        this.y =y;
        this.z = z;
    }

}
