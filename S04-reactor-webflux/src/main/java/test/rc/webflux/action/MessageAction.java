package test.rc.webflux.action;

import test.rc.webflux.handler.MonoMessageHandler;
import test.rc.webflux.vo.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.beans.PropertyEditorSupport;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageAction {

    private static final DateTimeFormatter LOCAL_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                LocalDate localDate = LocalDate.parse(text, LOCAL_DATE_FORMAT);
                Instant instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
                super.setValue(Date.from(instant));
            }
        });
    }

    @Autowired
    private MonoMessageHandler messageHandler;

    @RequestMapping( "/list")
    public Flux<Message> list(Message message){
        List<Message> messageList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Message message1 = new Message();
            message1.setTitle(i+"i");
            messageList.add(message1);
        }
        return Flux.fromIterable(messageList);
    }

    @RequestMapping( "/echo")
    public Object echo(Message message) {
        return messageHandler.echoHandler(message);
    }
}
