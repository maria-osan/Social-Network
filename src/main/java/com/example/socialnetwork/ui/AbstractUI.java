package com.example.socialnetwork.ui;

import com.example.socialnetwork.service.ServiceInterface;

public abstract class AbstractUI implements UI{
    ServiceInterface srv;

    public AbstractUI(ServiceInterface srv) {
        this.srv = srv;
    }
}
