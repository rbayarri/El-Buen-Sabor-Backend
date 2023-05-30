package com.lacodigoneta.elbuensabor.services;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ImageServiceFactory implements FactoryBean<ImageService> {

    private ApplicationContext applicationContext;

    @Override
    public ImageService getObject() {
        return null;
    }

    @Override
    public Class<?> getObjectType() {
        return ImageService.class;
    }

    @Override
    public boolean isSingleton() {
        return FactoryBean.super.isSingleton();
    }

    public ImageService getObject(boolean isFile) {

        if (isFile) {
            return applicationContext.getBean(ImageFileService.class);
        }
        return applicationContext.getBean(ImageUrlService.class);


    }
}
