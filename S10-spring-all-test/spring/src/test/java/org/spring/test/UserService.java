package org.spring.test;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserService {

    @Transactional
    public void startTransactional() {
        System.out.println("testTransactional");
    }

}
