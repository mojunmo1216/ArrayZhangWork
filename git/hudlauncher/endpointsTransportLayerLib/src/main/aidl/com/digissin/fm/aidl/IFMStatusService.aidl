package com.digissin.fm.aidl;
interface IFMStatusService{   
        boolean isFMOpen();
        boolean TakeOnFM();
        boolean TakeOffFM();
        boolean SetFMRate(float rate);
}