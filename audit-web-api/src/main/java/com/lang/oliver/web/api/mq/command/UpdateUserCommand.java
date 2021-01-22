package com.lang.oliver.web.api.mq.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


/**
 * 事件或者命令是发生的事，不能进行修改，所以不提供set方法
 */
@Getter
public class UpdateUserCommand extends BaseCommand {

    public UpdateUserCommand(String id, Long createTime, String key, UpdateUserCommand.UpdateUser payload,
                             BaseMessageHeader baseEventHeader) {
        this.payload = payload;
        this.key = key;
        this.createTime = createTime;
        this.id = id;
        this.header = baseEventHeader;
    }



    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateUser {
        private Long id;
        private String name;
        private String address;
    }
}
