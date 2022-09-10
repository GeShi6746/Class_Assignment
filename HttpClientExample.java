import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class HttpClientExample{
    public static void main(String[] args){
        List dateList = CalDates("0,1,2,3,4,5,6", 120);
        //Date date = new Date();
        System.out.println(dateList);
        //double d = 114.145333333;
        //d = (double) Math.round(d * 100) / 100;
        //System.out.println(d);
    }

    public static List CalDates(String weekDays, int days) {
        String[] weekDay;
        String delimeter1 = ",";
        weekDay = weekDays.split(delimeter1);
        ArrayList<LocalDate> localDates = new ArrayList<>();
        int weeks  = days / weekDay.length + 1;
        for (String s : weekDay) {
            LocalDate date;
            if (s.equals("0")) {
                date = LocalDate.of(2022, 4, 1).with(TemporalAdjusters.next(DayOfWeek.of(7)));
            } else {
                date = LocalDate.of(2022, 4, 1).with(TemporalAdjusters.next(DayOfWeek.of(Integer.parseInt(s))));
            }
            localDates.add(date);
            for (int m = 1; m < weeks; m++) {
                Calendar cal = Calendar.getInstance();
                ZonedDateTime zonedDateTime = date.atStartOfDay(ZoneId.systemDefault());
                cal.setTime(Date.from(zonedDateTime.toInstant()));
                cal.add(Calendar.DATE, m * 7);
                localDates.add(cal.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }
        }
        Collections.sort(localDates);
        ArrayList<LocalDate> dates = new ArrayList<>();
        for (int n=0; n<days; n++){
            LocalDate date = localDates.get(n);
            dates.add(date);
        }
        return dates;
    }
}