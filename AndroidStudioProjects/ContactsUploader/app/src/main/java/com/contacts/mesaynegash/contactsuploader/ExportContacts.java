package com.contacts.mesaynegash.contactsuploader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class ExportContacts extends Activity
{


    ProgressDialog progress;
    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_contacts);



        getVCF();
        progress = new ProgressDialog(ExportContacts.this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        String filename="Contacts.csv";
        File filelocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), filename);
        Uri path = Uri.fromFile(filelocation);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
// set the type to 'email'
        emailIntent .setType("vnd.android.cursor.dir/email");
        String to[] = {""};
        emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
// the attachment
        emailIntent .putExtra(Intent.EXTRA_STREAM, path);
// the mail subject
        emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Phone contacts backup");
        startActivity(Intent.createChooser(emailIntent , "Send email..."));
        progress.dismiss();

    }


    public void getVCF() {
        final String vfile = "Contacts.csv";
        Cursor phones = ExportContacts.this.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
                null, null);


        if (phones != null && phones.moveToFirst()) {

            do {

                String lookupKey = phones.getString(phones
                        .getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));

                Uri uri = Uri.withAppendedPath(
                        ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
                AssetFileDescriptor fd;
                try {
                    fd = ExportContacts.this.getContentResolver().openAssetFileDescriptor(uri,
                            "r");
                    FileInputStream fis = fd.createInputStream();
                    byte[] buf = new byte[(int) fd.getDeclaredLength()];
                    fis.read(buf);
                    String VCard = new String(buf);
                    String path = Environment.getExternalStorageDirectory()
                            .toString() + File.separator + vfile;
                    FileOutputStream mFileOutputStream = new FileOutputStream(path,
                            true);
                    mFileOutputStream.write(VCard.toString().getBytes());
                    phones.moveToNext();

                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            } while (phones.moveToNext());
        }
        else {

        }
    }


}