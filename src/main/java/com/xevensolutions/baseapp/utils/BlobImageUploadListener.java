package com.xevensolutions.baseapp.utils;




public interface BlobImageUploadListener {

    void onCompletion(boolean isSuccess, String blobName, String blobPath);


}
