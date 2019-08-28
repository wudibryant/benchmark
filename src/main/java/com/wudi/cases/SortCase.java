package com.wudi.cases;
import com.wudi.Case;
/**
 * 1.快排和归并快慢
 * 2.自己实现的归并排序和Arrays.sort对比
 */
import com.wudi.annotations.Benchmark;
import com.wudi.annotations.Measurement;

import java.util.Arrays;
import java.util.Random;

@Measurement(iterations = 10,group = 3)
public class SortCase implements Case {
    public void quickSort(int[] a){
        quickSortInternal(a,0,a.length-1);
    }
    private void quickSortInternal(int[]a,int low,int high){
        if(high<=low){
            return;
        }
        //基准值最终所在下标
        int[] pivotIndex=parition(a,low,high);
        quickSortInternal(a,low,pivotIndex[0]);
        quickSortInternal(a,pivotIndex[1],high);
    }
    private void swap(int[]a,int i,int j){
        int t=a[i];
        a[i]=a[j];
        a[j]=t;
    }
    private int[]parition(int[]a,int low,int high){
         int pivot=a[high];
         int less=low;
         int more=high;
         int i=low;
         while (i<more){
             if(a[i]==pivot){
                 i++;
             }else if(a[i]<pivot){
                 swap(a,i,less);
                 i++;
                 less++;
             }else {
                 while (more>i && a[more]>pivot){
                     more--;
                 }
                 swap(a,i,more);
             }
         }
         return new int[]{less-1,more};
    }
    public void mergeSort(int[]a){
        mergeSortInternal(a,0,a.length);
    }
    private void  mergeSortInternal(int[]a,int low,int high){
        if(high<=low+1){
            return;
        }
        int mid=(low+high)>>1;
        mergeSortInternal(a,low,mid);
        mergeSortInternal(a,mid,high);
        merge(a,low,mid,high);
    }
    private void merge(int []a,int low,int mid,int high){
        int n=high-low;
        int[]extra=new int[n];
        int i=low;
        int j=mid;
        int k=0;
        while (i<mid&&j<high){
            if(a[i]<=a[j]){
                extra[k++]=a[i++];
            }else {
                extra[k++]=a[j++];
            }
        }
        while (i<mid){
            extra[k++]=a[i++];
            }
        while (j<high){
            extra[k++]=a[j++];
        }
        System.arraycopy(extra,0,a,low,n);
    }
    @Benchmark
    public void textQuickSort(){
        int[]a=new int[100000];
        Random random=new Random(20190713);
        for (int i=0;i<a.length;i++){
            a[i]=random.nextInt(100000);
        }
        quickSort(a);
    }
    @Benchmark
    public void textMergeSort(){
        int[]a=new int[100000];
        Random random=new Random(20190713);
        for (int i=0;i<a.length;i++){
            a[i]=random.nextInt(100000);
        }
        mergeSort(a);
    }
    @Benchmark
    public void textArraysSort(){
        int[]a=new int[100000];
        Random random=new Random(20190713);
        for (int i=0;i<a.length;i++){
            a[i]=random.nextInt(100000);
        }
        Arrays.sort(a);
    }
}
