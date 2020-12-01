package cm.zm.main;

import cm.zm.domain.Article;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 自动访问CSDN文章链接。
 */
public class CSDNCrawler extends Thread{

    private static String userId = "weixin_44832837";

    private static Integer countView= 0 ;

    private static List<Article> urls = new ArrayList<Article>();
    private String threadName;

    static {
        getArticleUrl();
    }
    public CSDNCrawler(String threadName){
        this.threadName = threadName;
    }
    @Override
    public void run(){

        // System.out.println(this.threadName+"\trunning");
            // ---------------------------------------------------访问每个链接---------------------------------------------------
            int j = 0;
        int size = urls.size();
        Article[] objects = urls.toArray(new Article[urls.size()]);
        for (int i = 0; i < urls.size(); i++) {
            try {
                int t= (int) (System.currentTimeMillis()%size);
                Random random  = new Random();
                Article s = objects[(Math.abs(size*(t+1) - t) + random.nextInt(size)) % size];
                doGet(s.getUrl());
                System.out.println(this.threadName+" 成功访问第" + (++j) + "个链接,共" + urls.size() + "个:" + s.getTitle());
                System.out.println("+++++++++++++++++++++++++++++");
                Thread.sleep(300 + (t+1)*20);
                ++countView;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

            // ---------------------------------------------------程序结束---------------------------------------------------
            System.out.println(this.threadName + "\t运行完毕，成功增加访问数：" + urls.size());
            System.out.println("总计增加：" + countView);
        try {
            Thread.sleep(1000*5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static InputStream doGet(String urlstr) throws IOException {
        URL url = new URL(urlstr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        InputStream inputStream = conn.getInputStream();
        return inputStream;
    }

    public static String inputStreamToString(InputStream is, String charset) throws IOException {
        byte[] bytes = new byte[1024];
        int byteLength = 0;
        StringBuffer sb = new StringBuffer();
        while ((byteLength = is.read(bytes)) != -1) {
            sb.append(new String(bytes, 0, byteLength, charset));
        }
        return sb.toString();
    }

    public static void getArticleUrl() {
        // ----------------------------------------------遍历每一页 获取文章链接----------------------------------------------
        final String homeUrl = "https://blog.csdn.net/" + userId + "/article/list/";// 后面加pageNum即可
        InputStream is;
        int totalPage = 1;
        String pageStr;
        StringBuilder curUrl = null;
        for (int i = 1; i <= totalPage; i++) {
            try {
                curUrl = new StringBuilder(homeUrl);
                curUrl.append(i);
                is = doGet(curUrl.toString());
                pageStr = inputStreamToString(is, "UTF-8");// 一整页的html源码
                Document pageDoc = Jsoup.parse(pageStr);
                Elements articleListDoc = pageDoc.select("div.article-list");
                if (articleListDoc.isEmpty()) {
                    break;
                }else {
                    totalPage++;
                }
                Elements articleList = articleListDoc.select("h4>a");
                List<Article> list = getArticleList(articleList);
                urls.addAll(list);
                System.out.println("finding page " + i);
                System.out.println(curUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static List<Article> getArticleList (Elements elements){
        List<Article> articles = new ArrayList<Article>();
        String url = "";
        String title = "";
        for (Element element : elements) {
            Article article = new Article();
            url = element.attr("href");
            title = element.ownText();
            article.setTitle(title);
            article.setUrl(url);
            articles.add(article);
        }
        return articles;
    }
}