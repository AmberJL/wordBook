package com.example.wordbook;



public class Main {
	public static String decodeUnicode(final String dataStr) {
	    int start = 0;
	    int end = 0;
	    try{
	    	 final StringBuffer buffer = new StringBuffer();
	 	    while (start > -1) {
	 	        end = dataStr.indexOf("\\u", start + 2);
	 	        String charStr = "";
	 	        if (end == -1) {
	 	            charStr = dataStr.substring(start + 2, dataStr.length());
	 	        } else {
	 	            charStr = dataStr.substring(start + 2, end);
	 	        }
	 	        char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
	 	        buffer.append(new Character(letter).toString());
	 	        start = end;
	 	    }
	 	    return buffer.toString();
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	   return "无释义";
	}

    // 在平台申请的APP_ID 详见 http://api.fanyi.baidu.com/api/trans/product/desktop?req=developer
    private static final String APP_ID = "20191011000340751";
    private static final String SECURITY_KEY = "1Be_XwfAsvkrGta1IPGN";
//    public static void main(String[] args) {
//    	System.out.println(toChinese("success"));
//    	System.out.println(toEnglish("好的"));
//    	System.out.println(toChinese("success"));
//    }
    public static String toChinese(String e){
    	  TransApi api = new TransApi(APP_ID, SECURITY_KEY);
          String query = e;
          String r = api.getTransResult(query, "auto", "zh");
//          System.out.println(r);
          String error = r.substring(2,7);
          String[] re = r.split(":");
          String res = re[re.length-1];
          res = res.substring(1,res.length()-4);
          if(!error.equals("error")) return decodeUnicode(res);
          else return "无释义";

    }
    public static String toEnglish(String c){
    	TransApi api = new TransApi(APP_ID, SECURITY_KEY);
        String query = c;
        String r = api.getTransResult(query, "auto", "en");
//        System.out.println(r);
        String error = r.substring(2,7);
        String[] re = r.split(":");
        String res = re[re.length-1];
        res = res.substring(1,res.length()-4);
        if(!error.equals("error")) return res;
        else return "无释义";
    }

}
