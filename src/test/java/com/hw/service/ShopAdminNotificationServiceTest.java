package com.hw.service;

import com.hw.entity.DeliverTask;
import com.hw.repo.DeliverTaskRepo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class ShopAdminNotificationServiceTest {
    @Mock
    OAuthService oAuthService;

    @Spy
    DeliverTaskRepo deliverTaskRepo;

    @Spy
    @InjectMocks
    ShopAdminNotificationService shopAdminNotificationService = new ShopAdminNotificationService();

    @Spy
    JavaMailSender sender;

    @Test
    public void saveDeliverRequest_one_admin() {
        Mockito.doReturn("a@gmail").when(oAuthService).getAdminList();
        shopAdminNotificationService.saveDeliverRequest();
        Mockito.verify(deliverTaskRepo, Mockito.times(1)).save(any(DeliverTask.class));
    }

    @Test
    public void saveDeliverRequest_multi_admin() {
        Mockito.doReturn("a@gmail,b@gmail,c@gmail").when(oAuthService).getAdminList();
        shopAdminNotificationService.saveDeliverRequest();
        Mockito.verify(deliverTaskRepo, Mockito.times(3)).save(any(DeliverTask.class));
    }

    @Test
    public void saveDeliverRequest_no_admin() {
        Mockito.doReturn("").when(oAuthService).getAdminList();
        shopAdminNotificationService.saveDeliverRequest();
        Mockito.verify(deliverTaskRepo, Mockito.times(0)).save(any(DeliverTask.class));
    }

    @Test
    public void saveDeliverRequest_no_admin_null() {
        Mockito.doReturn(null).when(oAuthService).getAdminList();
        shopAdminNotificationService.saveDeliverRequest();
        Mockito.verify(deliverTaskRepo, Mockito.times(0)).save(any(DeliverTask.class));
    }

    @Test
    public void scanPendingDeliverTask_empty_task() {
        Mockito.doReturn(Collections.emptyList()).when(deliverTaskRepo).findAll();
        List<DeliverTask> type = shopAdminNotificationService.scanPendingDeliverTask(10000L, "type");
        Assert.assertEquals(0, type.size());
    }

    @Test
    public void scanPendingDeliverTask_multi_task_same_temp_dif_deliver_to() {
        DeliverTask deliverTask = DeliverTask.create("a", "type");
        deliverTask.setCreatedAt(Date.from(Instant.now()));
        deliverTask.setId(1L);
        DeliverTask deliverTask1 = DeliverTask.create("b", "type");
        deliverTask1.setCreatedAt(Date.from(Instant.now().plusMillis(1000)));
        deliverTask1.setId(2L);

        DeliverTask deliverTask2 = DeliverTask.create("c", "type");
        deliverTask2.setCreatedAt(Date.from(Instant.now().plusMillis(2000)));
        deliverTask2.setId(3L);

        Mockito.doReturn(Arrays.asList(deliverTask, deliverTask1, deliverTask2)).when(deliverTaskRepo).findAll();

        Mockito.doReturn(deliverTask).when(deliverTaskRepo).save(any());
        List<DeliverTask> type = shopAdminNotificationService.scanPendingDeliverTask(10000L, "type");
        Assert.assertEquals(3, type.size());
    }

    @Test
    public void scanPendingDeliverTask_multi_task() {
        DeliverTask deliverTask = DeliverTask.create("a", "type");
        deliverTask.setCreatedAt(Date.from(Instant.now()));
        deliverTask.setId(1L);
        DeliverTask deliverTask1 = DeliverTask.create("a", "type");
        deliverTask1.setCreatedAt(Date.from(Instant.now().plusMillis(1000)));
        deliverTask1.setId(2L);

        DeliverTask deliverTask2 = DeliverTask.create("a", "type");
        deliverTask2.setCreatedAt(Date.from(Instant.now().plusMillis(2000)));
        deliverTask2.setId(3L);

        Mockito.doReturn(Arrays.asList(deliverTask, deliverTask1, deliverTask2)).when(deliverTaskRepo).findAll();

        Mockito.doReturn(deliverTask).when(deliverTaskRepo).save(any());
        List<DeliverTask> type = shopAdminNotificationService.scanPendingDeliverTask(10000L, "type");
        Assert.assertEquals(1, type.size());
    }

}