package com.github.javarar.lucky.ticket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class LuckyTicket extends RecursiveTask<List<List<Integer>>> {
    final int seqThreshold;
    private final long start;
    private final long end;
    private final Integer size;
    
    public LuckyTicket(Integer seqThreshold, Long start, Long end, Integer size) {
        this.seqThreshold = seqThreshold;
        this.start = start;
        this.end = end;
        this.size = size;
    }
    
    public static void main(String[] args) {
        System.out.println(luckyTicketProbability(8));
    }
    
    public static int luckyTicketProbability(int serialNumberLength) {
        if (serialNumberLength % 2 != 0){
            throw new IllegalArgumentException("Странные у вас билеты, считайте - все счастливые");
        }
        long start = System.currentTimeMillis();
        long numberOfTickets = (long) Math.pow(10, serialNumberLength);
        LuckyTicket luckyTicket = new LuckyTicket((int)Math.sqrt(numberOfTickets), 0L, numberOfTickets, serialNumberLength);
        luckyTicket.fork();
        List<List<Integer>> join = luckyTicket.join();
        System.out.println(System.currentTimeMillis() - start);
        System.out.println(numberOfTickets);
        return join.size();
    }
    
    @Override
    protected List<List<Integer>> compute() {
        if ((end - start) < seqThreshold) {
            List<List<Integer>> list = new ArrayList<>();
            for (long i = start; i < end; i++) {
                int firstHalf = sumDigits(i / seqThreshold);
                int secondHalf = sumDigits(i % seqThreshold);
                if (firstHalf == secondHalf) {
                    list.add(getList(i, size));
                }
            }
            return list;
        } else {
            LuckyTicket subLuckyTicket1 = new LuckyTicket( seqThreshold, start, start + (end - start) / 2, size);
            LuckyTicket subLuckyTicket2 = new LuckyTicket(seqThreshold, start + (end - start) / 2, end, size);
            List<List<Integer>> result = new ArrayList<>();
            subLuckyTicket1.fork();
            subLuckyTicket2.fork();
            result.addAll(subLuckyTicket1.join());
            result.addAll(subLuckyTicket2.join());
            return result;
        }
    }
    
    private List<Integer> getList(long i, int serialNumberLength) {
        String s = String.format("%0" + serialNumberLength + "d", i);
        return Arrays.stream(s.split("")).map(Integer::valueOf).collect(Collectors.toList());
    }
    
    private int sumDigits(long i) {
        int sum = 0;
        while (i > 0) {
            sum += i % 10;
            i /= 10;
        }
        return sum;
    }
}