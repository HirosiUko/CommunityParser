import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Throws(NoSuchAlgorithmException::class, KeyManagementException::class)
fun setSSL() {
    val trustAllCerts = arrayOf<TrustManager>(
        object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate>?, authType: String?) {
                chain?.forEach { it.checkValidity() }
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate>?, authType: String?) {
                chain?.forEach { it.checkValidity() }
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        }
    )
    val sc = SSLContext.getInstance("SSL")
    sc.init(null, trustAllCerts, SecureRandom())
    HttpsURLConnection.setDefaultHostnameVerifier { hostname, session -> true }
    HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
}

class QueryEngine {
    private val TEST_ARTICLE_URL = "https://m.clien.net/service/board/news/17884635?od=T31&po=0&category=0&groupCd="

    // Board Const Variable
    private val MAIN_SELECTOR = "body > div.nav_container > div.content_list > a:nth-child(n)"
    private val TITLE_SELECTOR = "div.list_title > div.list_subject > span:nth-child(2)"
    private val ID_ATTR = "data-board-sn"
    private val AUTHOR_ATTR = "data-author-id"
    private val COMMENT_CNT_ATTR = "data-comment-count"
    private val LINK_ATTR = "href"
    private val SITE_PREFIX = "https://m.clien.net"

    private val boardNameMap = mapOf(
        "모두의공원" to "$SITE_PREFIX/service/board/park",
        "새로운소식" to "$SITE_PREFIX/service/board/news",
        "알뜰구매" to "$SITE_PREFIX/service/board/jirum"
    )

    // Article Const Variable
    private val ARTICLE_TITLE_SELECTOR = "body > div.nav_container > div.content_view > div.post_title > div > span"
    private val ARTICLE_SELECTOR =
        "body > div.nav_container > div.content_view > div.post_view > div.post_content > article"
    private val AUTHOR_NICKNAME_SELECTOR =
        "body > div.nav_container > div.content_view > div.post_view > div.post_contact > span.contact_name > span"
    private val ARTICLE_CREATE_TIME_SELECTOR = "#post-time > span.time"
    private val ARTICLE_VIEW_COUNTER_SELECTOR =
        "body > div.nav_container > div.content_view > div.post_view > div.post_information > div.post_time > div.view_count"
    private val ARTICLE_FAVORITE_COUNTER_SELECTOR = "#comment-head > div.symph_area > button.symph_count > strong"
    private val COMMENTS_SELECTOR = "#comment-div > div.comment.inline_link.ad_banner > div:nth-child(n)"
    private val COMMENT_AUTHOR_SELECTOR = "div.comment_info > div.post_contact > span.contact_name > span"
    private val COMMENT_AUTHOR_ALT_SELECTOR = "div.comment_info > div.post_contact > span.contact_name > span > img"
    private val COMMENT_DATETIME_SELECTOR = "#time > span"
    private val COMMENT_TEXT_SELECTOR = "div.comment_content > div"

    fun queryBoard(title: String = "모두의공원"): Single<ArrayList<Any>> {
        val url = boardNameMap[title]
        return Single.fromCallable {
            setSSL()
            val boardList: ArrayList<Any> = ArrayList()
            try {
                val doc: Document = Jsoup.connect(url).userAgent("Mozilla").get()
                val contentElements: Elements = doc.select(MAIN_SELECTOR)
                for (elem in contentElements) {
                    val id = elem.attr(ID_ATTR)
                    if (id.isNullOrEmpty()) continue
                    val title = elem.select(TITLE_SELECTOR).text()
                    val author = elem.attr(AUTHOR_ATTR)
                    val commentCnt = elem.attr(COMMENT_CNT_ATTR)
                    val link = SITE_PREFIX + elem.attr(LINK_ATTR)
                    boardList.add(Board(id, title, author, commentCnt, link))
                }
            }catch (e : Exception)
            {
                println(e.toString()+"\n"+e.stackTraceToString())
            }
            boardList
        }
    }

    fun queryArticle(url: String = TEST_ARTICLE_URL): Single<ArrayList<ArticleVo>> {
        return Single.fromObservable(Observable.create {
            val articleList: ArrayList<ArticleVo> = ArrayList()

            try {
                val doc: Document = Jsoup.connect(url).userAgent("Mozilla").get()
                val title = doc.select(ARTICLE_TITLE_SELECTOR).text()
                val articleText: Elements = doc.select(ARTICLE_SELECTOR)
                val authorNickName = doc.select(AUTHOR_NICKNAME_SELECTOR).text()
                val articleDateTime = doc.select(ARTICLE_CREATE_TIME_SELECTOR).text()
                val articleViewCount = doc.select(ARTICLE_VIEW_COUNTER_SELECTOR).text()
                val articleFavoriteCount = doc.select(ARTICLE_FAVORITE_COUNTER_SELECTOR).text()

                val article = ArticleVo(
                    articleTitle = title,
                    articleText = articleText.toString(),
                    authorNickName = authorNickName,
                    articleDateTime = articleDateTime,
                    articleViewCount = articleViewCount,
                    articleFavoriteCount = articleFavoriteCount
                )

                val commentElements: Elements = doc.select(COMMENTS_SELECTOR)
                for (commentElement in commentElements) {
                    var commentAuthor = commentElement.select(COMMENT_AUTHOR_SELECTOR).text()
                    if (!commentAuthor.isNullOrEmpty()) {
                        commentAuthor = commentElement.select(COMMENT_AUTHOR_ALT_SELECTOR).attr("alt")
                    }
                    val commentDateTime = commentElement.select(COMMENT_DATETIME_SELECTOR).text()
                    var commentText = commentElement.select(COMMENT_TEXT_SELECTOR).text()
                    if (commentElement.attr("class").contains("re")) {
                        commentText = "reply : $commentText"
                    }
                    val comment = CommentVo(commentAuthor, commentDateTime, commentText)
                    article.comments.add(comment)
                }
                articleList.add(article)
            }catch (e: Exception)
            {
                println(e.toString()+"\n"+e.stackTraceToString())
            }
            it.onNext(articleList)
            it.onComplete()
        })
    }
}


