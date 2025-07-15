package org.onlybuns.model;


import jakarta.persistence.*;

@Entity
@Table(name = "post_like")
public class PostLikeUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "like_id")
    private Like like;

    @Column(name = "username")
    private String username;

   // @Version
    //private Integer version=0;
    public PostLikeUser(Post post, Like like, String username) {
        this.post = post;
        this.like = like;
        this.username = username;
    }

    public PostLikeUser() {

    }

    public Long getId() {
        return id;
    }

    public Post getPost() {
        return post;
    }

    public Like getLike() {
        return like;
    }

    public String getUsername() {
        return username;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLike(Like like) {
        this.like = like;
    }

  /*  public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }*/
}
