//
// Copyright (c) 2019 Rosberg Linhares (rosberglinhares@gmail.com)
// Licensed under the MIT license. See LICENSE file in the project root for full license information.
//

package io.gazeui.collections;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class Lists {
    
    private Lists() {
        // No instances allowed
    }
    
    public static <T> List<T> longestCommonSubsequence(List<T> list1, List<T> list2,
            Comparator<? super T> comparator) {
        int[][] tabulationData = new int[list1.size() + 1][list2.size() + 1];
        
        for (int row = tabulationData.length - 2; row >= 0; row--) {
            for (int col = tabulationData[0].length - 2; col >= 0; col--) {
                if (comparator.compare(list1.get(row), list2.get(col)) == 0) {
                    tabulationData[row][col] = tabulationData[row + 1][col + 1] + 1;
                } else {
                    tabulationData[row][col] = Math.max(tabulationData[row][col + 1],
                            tabulationData[row + 1][col]);
                }
            }
        }
        
        int row = 0;
        int col = 0;
        List<T> result = new ArrayList<>(tabulationData[0][0]);
        
        while (row <= tabulationData.length - 2 && col <= tabulationData[0].length - 2) {
            if (comparator.compare(list1.get(row), list2.get(col)) == 0) {
                result.add(list1.get(row));
                
                row++;
                col++;
            } else {
                if (tabulationData[row][col + 1] > tabulationData[row + 1][col]) {
                    col++;
                } else {
                    row++;
                }
            }
        }
        
        return result;
    }
    
    public static <T, K, V> Map<K, V> toMap(List<T> list, Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends V> valueMapper) {
        
        // We could use the Stream API, but the Collectors#toMap method does a merge operation
        // between map keys, which we don't want to be done due to performance reasons.
        
        Map<K, V> result = new HashMap<>(list.size());
        
        for (T element : list) {
            result.put(keyMapper.apply(element), valueMapper.apply(element));
        }
        
        return result;
    }
}