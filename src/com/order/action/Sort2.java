package com.order.action;

public class Sort2 {

	/**
	 * 冒泡排序
	 *  比较相邻的元素。如果第一个比第二个大，就交换他们两个
	 *  对每一对相邻元素做同样的工作，从开始第一对到结尾的最后一对。在这一点，最后的元素应该会是最大的数
	 *  针对所有的元素重复以上的步骤，除了最后一个
	 *  持续每次对越来越少的元素重复上面的步骤，直到没有任何一对数字需要比较
	 * @param numbers 需要排序的整型数组
	 */
	public static int[] bubbleSort(int[] numbers){
		int bigger = 0;
		int length = numbers.length;
		
		for(int i = 0; i < length; i++){
			for(int j = 0; j < length - 1; j++){
				if(numbers[j] > numbers[j+1]){
					bigger = numbers[j];
					numbers[j] = numbers[j+1];
					numbers[j+1] = bigger;
				}
			}
		}
		return numbers;
		
	}
	
	/**
	 * 选择排序算法-找到最小的放到最前面
     * 在未排序序列中找到最小元素，存放到排序序列的起始位置  
     * 再从剩余未排序元素中继续寻找最小元素，然后放到排序序列末尾。 
     * 以此类推，直到所有元素均排序完毕。 
     * @param numbers 需要排序的整型数组
	 */
	
	public static int[] selectSort(int[] numbers){
		int temp  = 0;
		int length = numbers.length;
		for(int i = 0; i < length; i++){
			int minIndex = i;
			for(int j = i;j < length; j++){
				if(numbers[j] < numbers[minIndex]){
					minIndex = j;
				}
			}
			if(minIndex != i){
				temp = numbers[minIndex];
				numbers[minIndex] = numbers[i];
				numbers[i] = temp;
			}
			
		}
		
		
		return numbers;
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
	public static int[] insertSort(int[] numbers){
		int current = 0;
	    int length = numbers.length;
	    for(int i = 0; i < length - 1; i++){
	    	current = numbers[i+1];
	    	int preIndex = i;
	    	while(preIndex >=0 && current < numbers[preIndex]){
	    		numbers[preIndex + 1] = numbers[preIndex];
	    		preIndex --;
	    	}
	    	numbers[preIndex + 1] = current;
	    }
		
		
		return numbers;
	}
	
	
	
	public static void main(String[] args) {
		int[] numbers = {1,33,445,63,67,678,4456,2,7,34,74,36,45};
		int[] result = selectSort(numbers);
		
		for(int i = 0;i<result.length;i++){
			System.out.println(result[i]);
		} 
	}
}
