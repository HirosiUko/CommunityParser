class ArticleVo (
    val articleTitle: String,
    val articleText: String,
    val authorNickName: String,
    val articleDateTime: String,
    val articleViewCount: String,
    val articleFavoriteCount: String
) {
    var comments = mutableListOf<CommentVo>()

    override fun toString(): String {
        return "$articleTitle, $articleText, $authorNickName, $articleDateTime, $articleViewCount, $articleFavoriteCount \n $comments"
    }
}

class CommentVo (val commentAuthor: String, val commentDateTime: String, val commentText: String) {
    override fun toString(): String {
        return "$commentAuthor, $commentDateTime, $commentText"
    }
}