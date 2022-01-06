package com.example.sportapp.observable;

import lombok.Data;

import java.util.Observable;
import java.util.Observer;

public class AsynchronicRequest {

    @Data
    public class Channel implements Observer {

        private String news;

        @Override
        public void update(Observable o, Object news) {
            this.setNews((String) news);
        }
    }

    public class Agency extends Observable {
        private String news;

        public void setNews(String news) {
            this.news = news;
            setChanged();
            notifyObservers(news);
        }
    }
}
