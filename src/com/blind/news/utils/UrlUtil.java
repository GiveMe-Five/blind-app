package com.blind.news.utils;

/**
 * Created by DELL on 2015/2/24.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlUtil {
	/*
	 * url:  <a .*? href=\"http://.*?>.*?/a>
	 *
	 *
	 *
	 * */

    public static void main(String args[]) {

    }


    /**
     * 提取新浪新闻列表：http://rss.sina.com.cn/news/marquee/ddt.xml
     * @param html 要解析的文档内容
     * @return 解析结果
     */
    public static ArrayList<String> getSinaNews(String html) {
        ArrayList<String> resultList = new ArrayList<String>();
        StringBuffer name=new StringBuffer();
        StringBuffer url=new StringBuffer();
        String t1="",t2="";

        String regexURL="<title>.*?/title>";
        Pattern p = Pattern.compile(regexURL);
        Matcher m = p.matcher(html);
        int i=0;
        while (m.find()) {
            String t=m.group();
            if(!"".equals(t.trim())){
                i+=1;
                t=t.replaceAll("<title>", "").replaceAll("</title>", "").replaceAll("<!\\[CDATA\\[","").replaceAll("]]>", "").trim();
                System.out.println(t);
                if(i>2){
                    t1=t1+t+",";
                }
            }
        }

        regexURL="<link>.*?/link>";
        p = Pattern.compile(regexURL);
        m = p.matcher(html);
        i=0;
        while (m.find()) {
            String t=m.group();
            if(!"".equals(t.trim())){
                i+=1;
                t=t.replaceAll("<link>", "").replaceAll("</link>", "").trim();
                System.out.println(t);
                if(i>2){
                    t2=t2+t+",";
                }
            }
        }

        String[] t1s=t1.split(",");
        String[] t2s=t2.split(",");
        for(int j=0;j<t1s.length;j++){
            if(!"[播客]".equals(t1s[j].substring(0,4))){
                name.append(t1s[j]).append(",");
                url.append(t2s[j]).append(",");
            }
        }

        resultList.add(name.toString());
        resultList.add(url.toString());
        return resultList;
    }

    /**
     * 提取搜狐新闻列表：http://news.sohu.com/rss/scroll.xml
     * @param html 要解析的文档内容
     * @return 解析结果
     */
    public static ArrayList<String> getSohuNews(String html) {
        ArrayList<String> resultList = new ArrayList<String>();
        StringBuffer name=new StringBuffer();
        StringBuffer url=new StringBuffer();
        String t1="",t2="",t3="";

        String regexURL="<title>.*?/title>";
        Pattern p = Pattern.compile(regexURL);
        Matcher m = p.matcher(html);
        int i=0;
        while (m.find()) {
            String t=m.group();
            if(!"".equals(t.trim())){
                i=i+1;
                t=t.replaceAll("<title>", "").replaceAll("</title>", "").replaceAll("<!\\[CDATA\\[","").replaceAll("]]>", "").trim();
                System.out.println(t);
                if(i>2){
                    t1=t1+t+",";
                }
            }
        }

        regexURL="<pubDate>.*?</pubDate>";
        p = Pattern.compile(regexURL);
        m = p.matcher(html);
        i=0;
        while (m.find()) {
            String t=m.group();
            if(!"".equals(t.trim())){
                t=t.replaceAll("<pubDate>", "").replaceAll("</pubDate>", "").trim();
                try{
                    t2=t2+DateUtil.parseDateFormat1(t)+",";
                }catch(Exception e){
                }
            }
        }

        regexURL="<link>.*?/link>";
        p = Pattern.compile(regexURL);
        m = p.matcher(html);
        i=0;
        while (m.find()) {
            String t=m.group();
            if(!"".equals(t.trim())){
                i+=1;
                t=t.replaceAll("<link>", "").replaceAll("</link>", "").trim();
                System.out.println(t);
                if(i>2){
                    t3=t3+t+",";
                }
            }
        }

        String[] t1s=t1.split(",");
        String[] t2s=t2.split(",");
        String[] t3s=t3.split(",");
        for(int j=0;j<t1s.length;j++){
            if(!"视频".equals(t1s[j].substring(0,2))){
                name.append(t1s[j]).append("("+t2s[j]+")").append(",");
                url.append(t3s[j]).append(",");
            }
        }
        //System.out.println(t1);
        //System.out.println(t2);

        resultList.add(name.toString());
        resultList.add(url.toString());
        return resultList;
    }

    /**
     * 提取腾讯新闻列表：http://news.qq.com/newsgn/rss_newsgn.xml
     * @param html 要解析的文档内容
     * @return 解析结果
     */
    public static ArrayList<String> getQqNews(String html) {
        ArrayList<String> resultList = new ArrayList<String>();
        StringBuffer name=new StringBuffer();
        StringBuffer url=new StringBuffer();
        String t1="",t2="",t3="";

        String regexURL="<title>.*?/title>";
        Pattern p = Pattern.compile(regexURL);
        Matcher m = p.matcher(html);
        int i=0;
        while (m.find()) {
            String t=m.group();
            if(!"".equals(t.trim())){
                i=i+1;
                t=t.replaceAll("<title>", "").replaceAll("</title>", "").replaceAll("<!\\[CDATA\\[","").replaceAll("]]>", "").trim();
                System.out.println(t);
                if(i>2){
                    t1=t1+t+",";
                }
            }
        }

        regexURL="<pubDate>.*?</pubDate>";
        p = Pattern.compile(regexURL);
        m = p.matcher(html);
        i=0;
        while (m.find()) {
            String t=m.group();
            if(!"".equals(t.trim())){
                t=t.replaceAll("<pubDate>", "").replaceAll("</pubDate>", "").trim();
                System.out.println(t);
                try{
                    t2=t2+DateUtil.parseDateFormat2(t)+",";
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }

        regexURL="<link>.*?/link>";
        p = Pattern.compile(regexURL);
        m = p.matcher(html);
        i=0;
        while (m.find()) {
            String t=m.group();
            if(!"".equals(t.trim())){
                i+=1;
                t=t.replaceAll("<link>", "").replaceAll("</link>", "").trim();
                System.out.println(t);
                if(i>2){
                    t3=t3+t+",";
                }
            }
        }

        String[] t1s=t1.split(",");
        String[] t2s=t2.split(",");
        String[] t3s=t3.split(",");
        for(int j=0;j<t1s.length;j++){
            name.append(t1s[j]).append("("+t2s[j]+")").append(",");
            url.append(t3s[j]).append(",");
        }

        resultList.add(name.toString());
        resultList.add(url.toString());

        return resultList;
    }


    /**
     * 提取凤凰新闻列表：http://news.ifeng.com/rss/index.xml
     * @param html 要解析的文档内容
     * @return 解析结果
     */
    public static ArrayList<String> getIfengNews(String html) {
        ArrayList<String> resultList = new ArrayList<String>();
        StringBuffer name=new StringBuffer();
        StringBuffer url=new StringBuffer();
        String t1="",t2="",t3="";

        String regexURL="<title>.*?/title>";
        Pattern p = Pattern.compile(regexURL);
        Matcher m = p.matcher(html);
        int i=0;
        while (m.find()) {
            String t=m.group();
            if(!"".equals(t.trim())){
                i=i+1;
                t=t.replaceAll("<title>", "").replaceAll("</title>", "").replaceAll("<!\\[CDATA\\[","").replaceAll("]]>", "").trim();
                System.out.println(t);
                if(i>2){
                    t1=t1+t+",";
                }
            }
        }

        regexURL="<pubDate>.*?</pubDate>";
        p = Pattern.compile(regexURL);
        m = p.matcher(html);
        i=0;
        while (m.find()) {
            String t=m.group();
            if(!"".equals(t.trim())){
                t=t.replaceAll("<pubDate>", "").replaceAll("</pubDate>", "").trim();
                System.out.println(t);
                try{
                    t2=t2+DateUtil.parseDateFormat1(t)+",";
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }

        regexURL="<link>.*?/link>";
        p = Pattern.compile(regexURL);
        m = p.matcher(html);
        i=0;
        while (m.find()) {
            String t=m.group();
            if(!"".equals(t.trim())){
                i+=1;
                t=t.replaceAll("<link>", "").replaceAll("</link>", "").replaceAll("<!\\[CDATA\\[","").replaceAll("]]>", "").trim();
                System.out.println(t);
                if(i>2){
                    t3=t3+t+",";
                }
            }
        }

        String[] t1s=t1.split(",");
        String[] t2s=t2.split(",");
        String[] t3s=t3.split(",");
        for(int j=0;j<t1s.length;j++){
            name.append(t1s[j]).append("("+t2s[j]+")").append(",");
            url.append(t3s[j]).append(",");
        }

        resultList.add(name.toString());
        resultList.add(url.toString());

        return resultList;
    }

    /**
     * 提取163新闻列表：http://news.163.com/special/00011K6L/rss_newstop.xml
     * @param html 要解析的文档内容
     * @return 解析结果
     */
    public static ArrayList<String> get163News(String html) {
        ArrayList<String> resultList = new ArrayList<String>();
        StringBuffer name=new StringBuffer();
        StringBuffer url=new StringBuffer();
        String t1="",t2="",t3="";

        String regexURL="<title>.*?/title>";
        Pattern p = Pattern.compile(regexURL);
        Matcher m = p.matcher(html);
        int i=0;
        while (m.find()) {
            String t=m.group();
            if(!"".equals(t.trim())){
                i=i+1;
                t=t.replaceAll("<title>", "").replaceAll("</title>", "").replaceAll("<!\\[CDATA\\[","").replaceAll("]]>", "").trim();
                System.out.println(t);
                if(i>2){
                    t1=t1+t+",";
                }
            }
        }

        regexURL="<pubDate>.*?</pubDate>";
        p = Pattern.compile(regexURL);
        m = p.matcher(html);
        i=0;
        while (m.find()) {
            String t=m.group();
            if(!"".equals(t.trim())){
                t=t.replaceAll("<pubDate>", "").replaceAll("</pubDate>", "").trim();
                System.out.println(t);
                try{
                    t2=t2+DateUtil.parseDateFormat2(t)+",";
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }

        regexURL="<link>.*?/link>";
        p = Pattern.compile(regexURL);
        m = p.matcher(html);
        i=0;
        while (m.find()) {
            String t=m.group();
            if(!"".equals(t.trim())){
                i+=1;
                t=t.replaceAll("<link>", "").replaceAll("</link>", "").replaceAll("<!\\[CDATA\\[","").replaceAll("]]>", "").trim();
                System.out.println(t);
                if(i>2){
                    t3=t3+t+",";
                }
            }
        }

        String[] t1s=t1.split(",");
        String[] t2s=t2.split(",");
        String[] t3s=t3.split(",");
        for(int j=0;j<t1s.length;j++){
            name.append(t1s[j]).append("("+t2s[j]+")").append(",");
            url.append(t3s[j]).append(",");
        }

        resultList.add(name.toString());
        resultList.add(url.toString());

        return resultList;
    }
    //////////////////////////////////////////////////////////////

    /**
     * 提取新浪新闻：http://finance.sina.com.cn/money/lcfa/20110809/115610285913.shtml
     * @param html 要解析的文档内容
     * @return 解析结果
     */
    public static ArrayList<String> getSinaNewsDetail(String html) {
        ArrayList<String> resultList = new ArrayList<String>();
        StringBuffer name=new StringBuffer();
        StringBuffer title=new StringBuffer();
        StringBuffer date=new StringBuffer();

        //正文
        String regexURL="<!-- 正文内容 begin -->.*?<!-- 正文内容 end -->";
        Pattern p = Pattern.compile(regexURL);
        Matcher m = p.matcher(html);
        while (m.find()) {
            String t=m.group();
            if(!"".equals(t.trim())){
                name.append(UrlUtil.OutHtml(t));
            }
        }
        resultList.add(name.toString()+"\n");

        //标题
        regexURL="<h1 id=\"artibodyTitle\".*?</h1>";
        p = Pattern.compile(regexURL);
        m = p.matcher(html);
        while (m.find()) {
            String t=m.group();
            if(!"".equals(t.trim())){
                title.append(UrlUtil.OutHtml(t));
            }
        }
        resultList.add(title.toString());

        //时间
        regexURL="<span id=\"pub_date\">.*?</span>";
        p = Pattern.compile(regexURL);
        m = p.matcher(html);
        while (m.find()) {
            String t=m.group();
            if(!"".equals(t.trim())){
                date.append(UrlUtil.OutHtml(t));
            }
        }
        resultList.add(date.toString());

        return resultList;
    }

    /**
     * 提取搜狐新闻：http://news.sohu.com/20110810/n315954879.shtml
     * @param html 要解析的文档内容
     * @return 解析结果
     */
    public static ArrayList<String> getSohuNewsDetail(String html) {
        ArrayList<String> resultList = new ArrayList<String>();
        StringBuffer name=new StringBuffer();
        StringBuffer title=new StringBuffer();
        StringBuffer date=new StringBuffer();

        //正文
        String regexURL="<!-- 正文 st -->.*?<!-- 正文 end -->";
        Pattern p = Pattern.compile(regexURL);
        Matcher m = p.matcher(html);
        while (m.find()) {
            String t=m.group();
            if(!"".equals(t.trim())){
                name.append(UrlUtil.OutHtml(t));
            }
        }
        resultList.add(name.toString()+"\n");

        //标题
        regexURL="<h1>.*?</h1>";
        p = Pattern.compile(regexURL);
        m = p.matcher(html);
        while (m.find()) {
            String t=m.group();
            if(!"".equals(t.trim())){
                title.append(UrlUtil.OutHtml(t));
            }
        }
        resultList.add(title.toString());

        //时间
        regexURL="<div class=\"r\">20.*?</div>";
        p = Pattern.compile(regexURL);
        m = p.matcher(html);
        while (m.find()) {
            String t=m.group();
            if(!"".equals(t.trim())){
                date.append(UrlUtil.OutHtml(t));
            }
        }
        resultList.add(date.toString());

        return resultList;
    }


    /**
     * 提取腾讯新闻：http://news.sohu.com/20110810/n315954879.shtml
     * @param html 要解析的文档内容
     * @return 解析结果
     */
    public static ArrayList<String> getQqNewsDetail(String html) {
        ArrayList<String> resultList = new ArrayList<String>();
        StringBuffer name=new StringBuffer();
        StringBuffer title=new StringBuffer();
        StringBuffer date=new StringBuffer();

        //正文
        String regexURL="<div id=\"Cnt-Main-Article-QQ\" bossZone=\"content\">.*?<!--来源、关键字微博-->";
        Pattern p = Pattern.compile(regexURL);
        Matcher m = p.matcher(html);
        while (m.find()) {
            String t=m.group();
            if(!"".equals(t.trim())){
                name.append(UrlUtil.OutHtml(t));
            }
        }
        resultList.add(name.toString()+"\n");

        //标题
        regexURL="<div class=\"hd\"><h1>.*?</h1>";
        p = Pattern.compile(regexURL);
        m = p.matcher(html);
        while (m.find()) {
            String t=m.group();
            if(!"".equals(t.trim())){
                title.append(UrlUtil.OutHtml(t));
            }
        }
        resultList.add(title.toString());

        //时间
        regexURL="<span class=\"pubTime\">20.*?</span>";
        p = Pattern.compile(regexURL);
        m = p.matcher(html);
        while (m.find()) {
            String t=m.group();
            if(!"".equals(t.trim())){
                date.append(UrlUtil.OutHtml(t));
            }
        }
        resultList.add(date.toString());

        return resultList;
    }

    /**
     * 提取凤凰新闻：http://news.ifeng.com/mil/4/detail_2011_08/11/8331362_0.shtml
     * @param html 要解析的文档内容
     * @return 解析结果
     */
    public static ArrayList<String> getIfengNewsDetail(String html) {
        ArrayList<String> resultList = new ArrayList<String>();
        StringBuffer name=new StringBuffer();
        StringBuffer title=new StringBuffer();
        StringBuffer date=new StringBuffer();

        //正文
        String regexURL="<!--mainContent begin-->.*?<!--mainContent end-->";
        Pattern p = Pattern.compile(regexURL);
        Matcher m = p.matcher(html);
        while (m.find()) {
            String t=m.group();
            if(!"".equals(t.trim())){
                name.append(UrlUtil.outTeshu(UrlUtil.OutHtml(t)));
            }
        }
        resultList.add(name.toString()+"\n");

        //标题
        regexURL="<h1 id=\"artical_topic\">.*?</h1>";
        p = Pattern.compile(regexURL);
        m = p.matcher(html);
        while (m.find()) {
            String t=m.group();
            if(!"".equals(t.trim())){
                title.append(UrlUtil.OutHtml(t));
            }
        }
        resultList.add(title.toString());

        //时间
        regexURL="<span>20.*?</span>";
        p = Pattern.compile(regexURL);
        m = p.matcher(html);
        while (m.find()) {
            String t=m.group();
            if(!"".equals(t.trim())){
                date.append(UrlUtil.OutHtml(t));
            }
        }
        resultList.add(date.toString());

        return resultList;
    }

    /**
     * 提取163新闻：http://news.163.com/11/0811/09/7B5SON0E00014JB6.html
     * @param html 要解析的文档内容
     * @return 解析结果
     */
    public static ArrayList<String> get163NewsDetail(String html) {
        ArrayList<String> resultList = new ArrayList<String>();
        StringBuffer name=new StringBuffer();
        StringBuffer title=new StringBuffer();
        StringBuffer date=new StringBuffer();

        //正文
        String regexURL="<p class=\"summary\">.*?</p><div>";
        Pattern p = Pattern.compile(regexURL);
        Matcher m = p.matcher(html);
        while (m.find()) {
            String t=m.group();
            if(!"".equals(t.trim())){
                name.append(UrlUtil.OutHtml(t));
            }
        }
        resultList.add(name.toString()+"\n");

        //标题
        regexURL="<h1 id=\"h1title\">.*?</h1>";
        p = Pattern.compile(regexURL);
        m = p.matcher(html);
        while (m.find()) {
            String t=m.group();
            if(!"".equals(t.trim())){
                title.append(UrlUtil.OutHtml(t));
            }
        }
        resultList.add(title.toString());

        //时间
        regexURL="<span class=\"info\" style=\"display:block;\">20.*?来源";
        p = Pattern.compile(regexURL);
        m = p.matcher(html);
        while (m.find()) {
            String t=m.group();
            if(!"".equals(t.trim())){
                t=t.replaceAll("来源", "").trim();
                date.append(UrlUtil.OutHtml(t));
            }
        }
        resultList.add(date.toString());

        return resultList;
    }

    /**
     * 去格式
     * @param
     * @return
     */
    public static String OutHtml(String para)
    {
        para=para.replaceAll("<a.*?>", "").replaceAll("</a>", "");
        para=para.replaceAll("<A.*?>", "").replaceAll("</A>", "");

        para=para.replaceAll("<span style=\"margin-right:300px;\">.*?</span>", "");
        para=para.replaceAll("<span.*?>", "").replaceAll("</span>", "");
        para=para.replaceAll("<SPAN.*?>", "").replaceAll("</SPAN>", "");

        para=para.replaceAll("<strong.*?>", "").replaceAll("</strong>", "");
        para=para.replaceAll("<STRONG.*?>", "").replaceAll("</STRONG>", "");

        para=para.replaceAll("<!--.*?>", "");

        para=para.replaceAll("<div class=\"blkComment otherContent_01\".*?</div>", "");
        para=para.replaceAll("<div style=.*?</div>", "");
        para=para.replaceAll("<div.*?>", "").replaceAll("</div>", "");
        para=para.replaceAll("<DIV.*?>", "").replaceAll("</DIV>", "");

        para=para.replaceAll("<iframe.*?iframe>", "");
        para=para.replaceAll("<IFRAME.*?IFRAME>", "");

        para=para.replaceAll("<center.*?>", "").replaceAll("</center>", "");
        para=para.replaceAll("<CENTER.*?>", "").replaceAll("</CENTER>", "");

        para=para.replaceAll("<table.*?</table>", "");
        para=para.replaceAll("<TABLE.*?</TABLE>", "");
        para=para.replaceAll("<table.*?>", "");
        para=para.replaceAll("<TABLE.*?>", "");

        para=para.replaceAll("<TD.*?>", "").replaceAll("</TD>", "");
        para=para.replaceAll("<TR.*?>", "").replaceAll("</TR>", "");
        para=para.replaceAll("td.*?>", "").replaceAll("</td>", "");
        para=para.replaceAll("<tr.*?>", "").replaceAll("</tr>", "");

        para=para.replaceAll("<FONT.*?>", "").replaceAll("</FONT>", "");
        para=para.replaceAll("<font.*?>", "").replaceAll("</font>", "");

        para=para.replaceAll("<script.*?</script>", "");
        para=para.replaceAll("<SCRIPT.*?</SCRIPT>", "");

        para=para.replaceAll("<img.*?>", "");
        para=para.replaceAll("<IMG.*?>", "");

        para=para.replaceAll("<P.*?>　　　　　", "\t\t");
        para=para.replaceAll("<P.*?>　　　　", "\t\t");
        para=para.replaceAll("<P.*?>　　　", "\t\t");
        para=para.replaceAll("<P.*?>　　", "\t\t");
        para=para.replaceAll("<P.*?>　", "\t\t");
        para=para.replaceAll("<P.*?>", "\t\t");
        para=para.replaceAll("<P.*?>", "\t\t");
        para=para.replaceAll("<p.*?>　　　　　", "\t\t");
        para=para.replaceAll("<p.*?>　　　　", "\t\t");
        para=para.replaceAll("<p.*?>　　　", "\t\t");
        para=para.replaceAll("<p.*?>　　", "\t\t");
        para=para.replaceAll("<p.*?>　", "\t\t");
        para=para.replaceAll("<p.*?>", "\t\t");
        para=para.replaceAll("<p.*?>", "\t\t");
        para=para.replaceAll("</p>", "\n").replaceAll("<p.*?>", "");
        para=para.replaceAll("</P>", "\n").replaceAll("<P.*?>", "");

        para=para.replaceAll("</b>", "").replaceAll("<b.*?>", "");
        para=para.replaceAll("</B>", "").replaceAll("<B.*?>", "");

        para=para.replaceAll("<br.*?>", "\n");
        para=para.replaceAll("<BR.*?>", "\n");

        para=para.replaceAll("<style.*?style>", "");
        para=para.replaceAll("<link.*?/>", "");
        para=para.replaceAll("&nbsp;", "");

        para=para.replaceAll("<li.*?>", "").replaceAll("</li>", "");
        para=para.replaceAll("<ul.*?>", "").replaceAll("</ul>", "");
        para=para.replaceAll("<h1.*?>", "").replaceAll("</h1>", "");
        para=para.replaceAll("<h2.*?</h2>", "");
        para=para.replaceAll("<h3.*?>", "").replaceAll("</h3>", "");

        para=para.replaceAll("<object.*?</object>", "");
        para=para.replaceAll("<OBJECT.*?</OBJECT>", "");

        para=para.replaceAll("<!-- 相关阅读*?相关阅读 end -->", "");

        para=para.replaceAll("-->", "");
        para=para.replaceAll("欢迎发表评论我要评论", "");
        para="\t\t"+para.trim();
        System.out.println(para);
        return para;
    }


    /**
     * 获取网页页面内容
     * @param URLStr 地址
     * @return 获取结果
     */
    public String getSourceContent(String  URLStr)
    {
        String sourceContent="";
        StringBuffer sb=new StringBuffer();
        try
        {
            URL newURL=new URL(URLStr);
            BufferedReader br=new BufferedReader(
                    new InputStreamReader(newURL.openStream()));

            String temp;
            while((temp=br.readLine())!=null)
            {
                sb.append(temp);
            }
            sourceContent=sb.toString();

        }
        catch(MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return sourceContent;
    }


    private static String outTeshu(String para){
        para=para.replaceAll("&ldquo;", "“");
        para=para.replaceAll("&mdash;", "—");
        para=para.replaceAll("&rdquo;", "”");
        para=para.replaceAll("&middot;", "·");
        para=para.replaceAll("&lsquo;", "‘");
        para=para.replaceAll("&rsquo;", "’");
        para=para.replaceAll("&hellip;", "…");

        return para;
    }

}
