package com.example.ImageApp;

import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class ImageViewModel  extends ViewModel {


    public MutableLiveData<List<Bitmap>> images;

    public ImageViewModel(){
        images = new MutableLiveData<List<Bitmap>>();
    }

    public List<Bitmap> getImages(){
        return images.getValue();
    }

    public void addImage(Bitmap bitmap) {
        List<Bitmap> newImages = getImages();
        newImages.add(bitmap);
        images.postValue(newImages);
    }

    public void setImages(List<Bitmap> bitmaps){
        images.postValue(bitmaps);
    }
}
