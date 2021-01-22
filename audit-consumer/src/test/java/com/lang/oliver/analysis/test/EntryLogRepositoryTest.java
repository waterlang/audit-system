package com.lang.oliver.analysis.test;

import com.lang.oliver.analysis.domain.EntryLog;
import com.lang.oliver.analysis.repository.EntryLogRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EntryLogRepositoryTest  {


    @Autowired
    private EntryLogRepository entryLogRepository;
    @Value("${spring.application.name}")
    private String appName;

    @Test
    public void shouldReturnNotNullWhenInsert(){
        EntryLog inputData = new EntryLog();
        inputData.setClassName(this.getClass().getSimpleName());
        inputData.setProjectName(appName);
        inputData.setCreateTime(new Date());
        inputData.setCustomerId(1);
        inputData.setMethodName("shouldReturnNotNullWhenInsert");
        inputData.setRequestTime(new Date());
        inputData.setParameters("parms");
        inputData.setResult("result");
        inputData.setTraceId(UUID.randomUUID().toString());
        entryLogRepository.insert(inputData);
        Assert.assertNotNull(inputData.getId());
    }
}
