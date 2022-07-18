package com.xevensolutions.baseapp.utils;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.xevensolutions.baseapp.R;

public class DataProvider {

    public static RequestOptions getGlideRequestOptions(boolean circularImage, boolean shouldOverrideDimensions) {
        RequestOptions requestOptions = new RequestOptions();
        if (circularImage)
            requestOptions.circleCrop();
        if (shouldOverrideDimensions) {

            requestOptions.override(200, 200);
        }

        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);

        //requestOptions.placeholder(R.drawable.image_placeholder);
        requestOptions.error(R.drawable.ic_baseline_image_24);
        return requestOptions;
    }


}
