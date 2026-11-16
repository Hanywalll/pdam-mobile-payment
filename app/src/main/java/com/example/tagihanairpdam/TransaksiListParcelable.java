package com.example.tagihanairpdam;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

public class TransaksiListParcelable implements Parcelable {

    private List<CustomerResponse.Transaksi> transaksiList;

    public TransaksiListParcelable(List<CustomerResponse.Transaksi> transaksiList) {
        this.transaksiList = transaksiList;
    }

    protected TransaksiListParcelable(Parcel in) {
        transaksiList = new ArrayList<>();
        in.readList(transaksiList, CustomerResponse.Transaksi.class.getClassLoader());
    }

    public static final Creator<TransaksiListParcelable> CREATOR = new Creator<TransaksiListParcelable>() {
        @Override
        public TransaksiListParcelable createFromParcel(Parcel in) {
            return new TransaksiListParcelable(in);
        }

        @Override
        public TransaksiListParcelable[] newArray(int size) {
            return new TransaksiListParcelable[size];
        }
    };

    public List<CustomerResponse.Transaksi> getTransaksiList() {
        return transaksiList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(transaksiList);
    }
}