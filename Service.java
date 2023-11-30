import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
@Slf4j
public class Service {

    @Autowired
    SimpMessagingTemplate socketTemplate;

    public void sendNotificationToChannel1(Object obj) {
        socketTemplate.convertAndSend(WebSocketConfig.CHANNEL1, obj);
    }

    public void sendNotificationToChannel2(Object obj) {
        socketTemplate.convertAndSend(WebSocketConfig.CHANNEL2, obj);
    }

}
