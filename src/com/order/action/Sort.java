package com.order.action;

public class Sort {

	/**
     * 冒泡排序-找到最大的放在最后面
     * 比较相邻的元素。如果第一个比第二个大，就交换他们两个。  
     * 对每一对相邻元素作同样的工作，从开始第一对到结尾的最后一对。在这一点，最后的元素应该会是最大的数。  
     * 针对所有的元素重复以上的步骤，除了最后一个。
     * 持续每次对越来越少的元素重复上面的步骤，直到没有任何一对数字需要比较。 
     * @param numbers 需要排序的整型数组
     */
    public static void bubbleSort(int[] numbers)
    {
        int temp = 0;
        int size = numbers.length;
        for(int i = 0 ; i < size-1; i ++)
        {
        for(int j = 0 ;j < size-1-i ; j++)
        {
            if(numbers[j] > numbers[j+1])  //交换两数位置
            {
            temp = numbers[j];
            numbers[j] = numbers[j+1];
            numbers[j+1] = temp;
            }
        }
        }
    }
    
    /**
     * 选择排序算法-找到最小的放到最前面
     * 在未排序序列中找到最小元素，存放到排序序列的起始位置  
     * 再从剩余未排序元素中继续寻找最小元素，然后放到排序序列末尾。 
     * 以此类推，直到所有元素均排序完毕。 
     * @param numbers
     */
    
    public static void selectSort(int[] numbers){
	    int size = numbers.length; //数组长度
	    int temp = 0 ; //中间变量
	    
	    for(int i = 0;i < size - 1;i++){
	    	int k = i;
	    	for(int j = size - 1; j > i; j--){
	    		if(numbers[j] < numbers[k]){
	    			k = j;
	    		}
	    	}
	    	temp = numbers[i];
	    	numbers[i] = numbers[k];
	    	numbers[k]= temp;
	    }
	}
    
    /**  
     * 插入排序
     * 
     * 从第一个元素开始，该元素可以认为已经被排序
     * 取出下一个元素，在已经排序的元素序列中从后向前扫描 
     * 如果该元素（已排序）大于新元素，将该元素移到下一位置  
     * 重复步骤3，直到找到已排序的元素小于或者等于新元素的位置  
     * 将新元素插入到该位置中  
     * 重复步骤2  
     * @param numbers  待排序数组
     */  
    public static void insertSort(int[] numbers){
	    int size = numbers.length;
	    int temp = 0 ;
	    int j =  0;
	    
	    for(int i = 0 ; i < size ; i++){
	        temp = numbers[i];
	        //假如temp比前面的值小，则将前面的值后移
	        for(j = i ; j > 0 && temp < numbers[j-1] ; j --){
	        	numbers[j] = numbers[j-1];
	        }
	        numbers[j] = temp;
	    }
    }
    
    /**希尔排序的原理:根据需求，如果你想要结果从大到小排列，它会首先将数组进行分组，然后将较大值移到前面，较小值
     * 移到后面，最后将整个数组进行插入排序，这样比起一开始就用插入排序减少了数据交换和移动的次数，可以说希尔排序是加强
     * 版的插入排序
     * 拿数组5, 2, 8, 9, 1, 3，4来说，数组长度为7，当increment为3时，数组分为两个序列
     * 5，2，8和9，1，3，4，第一次排序，9和5比较，1和2比较，3和8比较，4和比其下标值小increment的数组值相比较
     * 此例子是按照从大到小排列，所以大的会排在前面，第一次排序后数组为9, 2, 8, 5, 1, 3，4
     * 第一次后increment的值变为3/2=1,此时对数组进行插入排序，
     *实现数组从大到小排
     */

        public static void shellSort(int[] data){
            int j = 0;
            int temp = 0;
            //每次将步长缩短为原来的一半
            for (int increment = data.length / 2; increment > 0; increment /= 2){
	            for (int i = increment; i < data.length; i++){
	                temp = data[i];
	                for (j = i; j >= increment; j -= increment){
		                if(temp > data[j - increment]){//如想从小到大排只需修改这里   
		                    data[j] = data[j - increment];
		                }else{
		                    break;
		                }
	                } 
	                data[j] = temp;
	            }
            }
        }
}
