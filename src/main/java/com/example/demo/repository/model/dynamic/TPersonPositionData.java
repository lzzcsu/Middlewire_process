package com.example.demo.repository.model.dynamic;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_person_position_data")
public class TPersonPositionData {
    @TableId
    private Integer ID;
    private String CARD_ID;
    private Date GET_DATE;
    private String TAG_ID;
    private String QUALITY;
    private int HAS_VISITED;
    private Date SAVE_TIME;

    public TPersonPositionData(String CARD_ID, Date GET_DATE, String TAG_ID, String QUALITY, int HAS_VISITED, Date SAVE_TIME) {
        this.CARD_ID = CARD_ID;
        this.GET_DATE = GET_DATE;
        this.TAG_ID = TAG_ID;
        this.QUALITY = QUALITY;
        this.HAS_VISITED = HAS_VISITED;
        this.SAVE_TIME = SAVE_TIME;
    }
}