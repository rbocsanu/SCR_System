package com.broker.social_companion_system.client;

import com.broker.social_companion_system.entities.Query;
import com.broker.social_companion_system.entities.ServiceQuery;
import com.broker.social_companion_system.entities.TaskQuery;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RandomSelectorService implements QuerySelectorService {

    private final Random rand = new Random();

    @Override
    public Query select(String request) {

        return new ServiceQuery(request, rand.nextInt(6), rand.nextInt(100,1000));
        /*
        if (rand.nextBoolean()) {
            return new ServiceQuery(request, rand.nextInt(6), rand.nextInt(100,1000));
        } else {
            return new TaskQuery(request, rand.nextInt(6), rand.nextInt(100,1000));
        }

         */

    }
}
