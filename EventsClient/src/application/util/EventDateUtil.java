package application.util;

import java.util.Date;

public class EventDateUtil {
    public static String getEventMonth(String month){
        if("1".equalsIgnoreCase(month)){
            return "IAN";
        }else if("2".equalsIgnoreCase(month)){
            return "FEB";
        }else if("3".equalsIgnoreCase(month)){
            return "MAR";
        }else if("4".equalsIgnoreCase(month)){
            return "APR";
        }else if("5".equalsIgnoreCase(month)){
            return "MAY";
        }else if("6".equalsIgnoreCase(month)){
            return "JUN";
        }else if("7".equalsIgnoreCase(month)){
            return "JLY";
        }else if("8".equalsIgnoreCase(month)){
            return "AUG";
        }else if("9".equalsIgnoreCase(month)){
            return "SEP";
        }else if("10".equalsIgnoreCase(month)){
            return "OCT";
        }else if("11".equalsIgnoreCase(month)){
            return "NOV";
        }else if("12".equalsIgnoreCase(month)){
            return "DEC";
        }
        return null;
    }
}
