package com.xevensolutions.baseapp.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.widget.Toast;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.LinkedList;

//created by @Talha Akbar

public class BlobManager {
    private String blobName;
    String fileNameFull;
    Uri fileUri;
    String fileExtension = "";
    String fileUrl;
    Context context;


    public BlobManager(Context context, Uri uri) {
        fileUri = uri;
        this.context = context;
        fileExtension = getExtension();
    }

    public BlobManager(Context context, Uri uri, boolean getExtension) {
        fileUri = uri;
        this.context = context;
    }

    public BlobManager(Context context, String path) {
        fileUri = Uri.fromFile(new File(path));
        try {
            fileExtension = path.substring(path.lastIndexOf("."));
        } catch (Exception ignored) {
        }
        this.context = context;
    }


    /*
    **Only use Shared Key authentication for testing purposes!** 
    Your account name and account key, which give full read/write access to the associated Storage account, 
    will be distributed to every person that downloads your app. 
    This is **not** a good practice as you risk having your key compromised by untrusted clients. 
    Please consult following documents to understand and use Shared Access Signatures instead. 
    https://docs.microsoft.com/en-us/rest/api/storageservices/delegating-access-with-a-shared-access-signature 
    and https://docs.microsoft.com/en-us/azure/storage/common/storage-dotnet-shared-access-signature-part-1 
    */
//    public static final String storageConnectionString = "DefaultEndpointsProtocol=https;"
//            + "AccountName=[ACCOUNT_NAME];"
//            + "AccountKey=[ACCOUNT_KEY]";
    private static final String storageContainer = "alfacare";
    private static final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=alcstorages;AccountKey=M/eyrhbKSvWfE4pkPwop1xwjlKJviIa1RDHSWwITWtoKSbXXR6nYKcH235I1lC4ZMyctEbUmagQ6va7GzU9CqA==;EndpointSuffix=core.windows.net";


    private static CloudBlobContainer getContainer() throws Exception {
        // Retrieve storage account from connection-string.

        CloudStorageAccount storageAccount = CloudStorageAccount
                .parse(storageConnectionString);

        // Create the blob client.
        CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

        // Get a reference to a container.
        // The container name must be lower case
        CloudBlobContainer container = blobClient.getContainerReference(storageContainer);

        return container;
    }

    public static String uploadImage(InputStream image, int imageLength, String extension) throws Exception {
        CloudBlobContainer container = getContainer();

        container.createIfNotExists();

        String imageName = randomString(10) + extension;

        CloudBlockBlob imageBlob = container.getBlockBlobReference(imageName);
        imageBlob.upload(image, imageLength);

        return imageName;

    }

    public void uploadFile(SetOnUploadListener listener) {
        try {
            final InputStream imageStream = context.getContentResolver().openInputStream(this.fileUri);
            final int imageLength = imageStream.available();

            final Handler handler = new Handler();

            Thread th = new Thread(new Runnable() {
                public void run() {

                    try {

                        final String imageName = uploadImage(imageStream, imageLength, fileExtension);

                        handler.post(new Runnable() {

                            public void run() {
                                fileUrl = Constants.BLOB_PATH + imageName;
                                listener.onUploadSuccess(fileUrl);
//                                Toast.makeText(context, "Image Uploaded Successfully. Name = " + imageName, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception ex) {
                        final String exceptionMessage = ex.getMessage();
                        handler.post(new Runnable() {
                            public void run() {
                                listener.onUploadFailed(ex);
//                                Toast.makeText(context, exceptionMessage, Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }
            });
            th.start();
        } catch (Exception ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public String uploadFileSimple() {
        try {
            final InputStream imageStream = context.getContentResolver().openInputStream(this.fileUri);
            final int imageLength = imageStream.available();

            try {
                final String imageName = uploadImage(imageStream, imageLength, fileExtension);
                return fileUrl = Constants.BLOB_PATH + "/" + storageContainer + "/" + imageName;
//                                Toast.makeText(context, "Image Uploaded Successfully. Name = " + imageName, Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {

                final String exceptionMessage = ex.getMessage();
                Toast.makeText(context, exceptionMessage, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
//            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }
        return "";

    }

    public String uploadFileSimple(SetOnUploadListener listener) {


        try {
            final InputStream imageStream = context.getContentResolver().openInputStream(this.fileUri);
            final int imageLength = imageStream.available();
            final Handler handler = new Handler();
            new Thread(() -> {
                try {
                    final String imageName = uploadImage(imageStream, imageLength, fileExtension);
                    handler.post(() ->
                            listener.onUploadSuccess(Constants.BLOB_PATH + imageName));
                } catch (Exception ex) {
                    handler.post(() -> {
                        listener.onUploadFailed(ex);
                    });
                    final String exceptionMessage = ex.getMessage();
                }
            }).start();
        } catch (Exception ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return "";

    }

    public String getExtension() {
        String filePath = "";

        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(fileUri, filePathColumn, null, null, null);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
//            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        }
        cursor.close();
        fileExtension = filePath.substring(filePath.lastIndexOf("."));
        return fileExtension;
    }


    public static String[] ListImages() throws Exception {
        CloudBlobContainer container = getContainer();

        Iterable<ListBlobItem> blobs = container.listBlobs();

        LinkedList<String> blobNames = new LinkedList<>();
        for (ListBlobItem blob : blobs) {
            blobNames.add(((CloudBlockBlob) blob).getName());
        }

        return blobNames.toArray(new String[blobNames.size()]);
    }

    public static void GetImage(String name, OutputStream imageStream, long imageLength) throws Exception {
        CloudBlobContainer container = getContainer();

        CloudBlockBlob blob = container.getBlockBlobReference(name);

        if (blob.exists()) {
            blob.downloadAttributes();

            imageLength = blob.getProperties().getLength();

            blob.download(imageStream);
        }
    }

    static final String validChars = "abcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(validChars.charAt(rnd.nextInt(validChars.length())));


        return Calendar.getInstance().getTimeInMillis() + sb.toString();
    }


    public interface SetOnUploadListener {
        void onUploadSuccess(String fileUrl);

        void onUploadFailed(Exception ex);
    }

}