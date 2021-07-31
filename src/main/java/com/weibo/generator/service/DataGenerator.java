package com.weibo.generator.service;

import com.github.javafaker.Faker;
import com.weibo.generator.entity.*;
import com.weibo.generator.network.Network;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

@Component
public class DataGenerator {

    private static Random rand;
    private static ArrayList<String> cities;
    private static ArrayList<User> users;
    private static ArrayList<Tag> tags;
    private static ArrayList<Weibo> weibos;
    private static ArrayList<Comment> comments;
    private static int cityCount;
    private static int userCount;
    private static int tagCount;
    private static int weiboCount;
    private static int commentCount;
    private static int sendNumberPerSecond = 65;
    private static int currentSendNumber = 0;
    private static ArrayList<String> tag_list = new ArrayList<>(
            (Arrays.asList(
                    "Happen here his seek site.",
                    "Enough together form push interview green far.",
                    "Dog score return heavy.",
                    "Team range shoulder customer hold.",
                    "Special behind heavy add performance relate.",
                    "Form modern us indicate drug.",
                    "Total hundred develop really.",
                    "Other health piece budget science difficult.",
                    "Name middle marriage break industry.",
                    "Order bank million concern on parent.",
                    "Care read because together event hard.",
                    "Poor public again recognize deep us.",
                    "Behind ten again player.",
                    "Hand improve interview responsibility.",
                    "Task understand particular wear listen how.",
                    "Your they method seat.",
                    "Color interview true school close.",
                    "Together season ask use mind.",
                    "Section information base add for.",
                    "Possible nothing score nearly sea cultural.",
                    "Success leader bag myself less whether firm movement.",
                    "Just about wish through often everyone.",
                    "May country oil others.",
                    "Put source recent do enough culture culture.",
                    "Finally subject fish mind whole agreement.",
                    "Management right point out instead suffer recognize.",
                    "Site mean position when agent such.",
                    "Southern lose recent.",
                    "Far network environment always foot lead.",
                    "Him but reflect third cost left institution.",
                    "Author method all affect project woman.",
                    "Happy himself mind church.",
                    "Picture history base information.",
                    "West argue charge.",
                    "Maybe hot record four drop whole already.",
                    "Should hope though hundred pass resource send.",
                    "World away fear example.",
                    "Read company pattern it together new kitchen grow.",
                    "History quickly finish present.",
                    "Find continue job network skin control past.",
                    "Some national free add common.",
                    "Best choose statement star.",
                    "Old PM wall toward interesting onto many.",
                    "Probably chair commercial by the range.",
                    "Well according dinner clear represent cut.",
                    "However floor entire environmental enter mean life.",
                    "Language best section service much leave hospital.",
                    "Take case leg real serve.",
                    "Down culture partner wait treatment reach with.",
                    "Affect range kid after."
            ))
    );

    private static ArrayList<String> agents = new ArrayList<>
            (Arrays.asList(
                    "Android 4.4.2",
                    "Android 7.0",
                    "Android 5.1.1",
                    "Android 7.1.1",
                    "Android 5.0.2",
                    "iPad; CPU iPad OS 7_1_2 like Mac OS X",
                    "iPhone; CPU iPhone OS 10_3_3 like Mac OS X",
                    "iPad; CPU iPad OS 10_3_4 like Mac OS X",
                    "iPhone; CPU iPhone OS 9_3_5 like Mac OS X",
                    "iPhone; CPU iPhone OS 10_3_3 like Mac OS X")
            );
    private static String[] genders = new String[2];

    private static Faker faker;

    private static void init() {
        rand = new Random();
        cities = new ArrayList<>();
        users = new ArrayList<>();
        tags = new ArrayList<>();
        weibos = new ArrayList<>();
        comments = new ArrayList<>();
        faker = new Faker();
        cityCount = faker.number().numberBetween(20, 81);
        userCount = faker.number().numberBetween(10, 20);
        tagCount = faker.number().numberBetween(10, 20);
        commentCount = faker.number().numberBetween(20, 30);
        weiboCount = faker.number().numberBetween(userCount, userCount * 2);
        genders[0] = "male";
        genders[1] = "female";

    }


    public static boolean generate(Network client) throws InterruptedException {
        init();
        generateBaseEntities();
        long startTime;
        long endTime;
        long sleepInSec;
        while (client.serverIsOk()) {
            if (client.hasControlMessage()) {
                client.sendControlSignal();
            }
            startTime = System.currentTimeMillis();

            sendNewRelations(client);
            endTime = System.currentTimeMillis();


            sleepInSec = 1000 - (endTime - startTime);

            if (sleepInSec < 0) {
                continue;
            }

            System.out.println("Thread will sleep for: " + sleepInSec / 1000);
            Thread.sleep(sleepInSec);
        }

        return true;

    }


    private static void generateBaseEntities() {
        // fill the available cities
        generateBaseCities();
        // fill the available weibo tags
        generateBaseTags();
        // fill the available users
        generateBaseUsers();
        // fill the available weibos
        generateBaseWeibos();
        // fill the available comments
        generateBaseComments();

    }


    private static void sendNewRelations(Network client) {
        currentSendNumber = sendNumberPerSecond;
        while (currentSendNumber > 0) {
            int dice = faker.number().numberBetween(1, 6);

            if (dice <= 2) {
                User newUser = new User(faker.name().name(), faker.idNumber().valid(), getRandomCity(), getRandomAge(), getRandomGender());
                int fansNumber = Math.min(faker.number().numberBetween(1, currentSendNumber), users.size() / 3);
                generateRelationUsers(newUser.getFans(), newUser, fansNumber);
                sendFans(client, newUser);
                users.add(newUser);
                currentSendNumber -= fansNumber;
            } else if (dice <= 4) {
                Weibo weibo = genNewWeibo();
                currentSendNumber -= 1;
                genSingleWeiboRelation(weibo);
                sendWeibo(client, weibo);
                weibos.add(weibo);
            } else {
                if (currentSendNumber < 2)
                    continue;
                Comment comment = genNewComment();
                currentSendNumber -= 2;
                genSingleCommentRelation(comment);
                sendComment(client, comment);
            }
        }
    }


    private static void sendComment(Network client, Comment comment) {

        Edge<User, Comment> authorEdge = new Edge<>(comment.getAuthor(), comment, RelationLabel.Author.getLabel(), faker.idNumber().invalid(), VertexLabel.Comment.name());
        client.send(authorEdge.toString());

        Edge<Comment, Weibo> replyEdge = new Edge<>(comment, comment.getReplyOf(), RelationLabel.ReplyOf.getLabel(), faker.idNumber().invalid(), VertexLabel.Weibo.name());
        client.send(replyEdge.toString());

        for (Weibo mentionedWeibo : comment.getMentionedWeibos()) {
            Edge<Comment, Weibo> mentionedEdge = new Edge<>(comment, mentionedWeibo, RelationLabel.Mentioned.getLabel(), faker.idNumber().invalid(), VertexLabel.Comment.name());
            client.send(mentionedEdge.toString());
        }

        for (User atUser : comment.getAtUsers()) {
            Edge<Comment, User> atEdge = new Edge<>(comment, atUser, RelationLabel.At.getLabel(), faker.idNumber().invalid(), VertexLabel.Comment.name());
            client.send(atEdge.toString());
        }
    }

    private static void sendWeibo(Network client, Weibo weibo) {
        Edge<User, Weibo> authorEdge = new Edge<>(weibo.getAuthor(), weibo, RelationLabel.Author.getLabel(), faker.idNumber().invalid(), VertexLabel.Weibo.name());
        client.send(authorEdge.toString());

        for (Weibo mentionedWeibo : weibo.getMentionedWeibos()) {
            Edge<Weibo, Weibo> mentionedEdge = new Edge<>(weibo, mentionedWeibo, RelationLabel.Mentioned.getLabel(), faker.idNumber().invalid(), VertexLabel.Weibo.name());
            client.send(mentionedEdge.toString());
        }

        for (User atUser : weibo.getAtUsers()) {
            Edge<Weibo, User> atEdge = new Edge<>(weibo, atUser, RelationLabel.At.getLabel(), faker.idNumber().invalid(), VertexLabel.Weibo.name());
            client.send(atEdge.toString());
        }

        for (Tag tag : weibo.getTags()) {
            Edge<Tag, Weibo> tagEdge = new Edge<>(tag, weibo, RelationLabel.BelongTo.getLabel(), faker.idNumber().invalid(), faker.team().creature());
            client.send(tagEdge.toString());
        }
    }

    private static void sendFans(Network client, User user) {
        for (User fan : user.getFans()) {
            Edge<User, User> fanEdge = new Edge<>(user, fan, RelationLabel.Fans.getLabel(), faker.idNumber().invalid(), faker.friends().character());
            client.send(fanEdge.toString());
        }
    }


    private static void generateBaseCities() {
        for (int i = 0; i < (cityCount / 2) + 1; i++) {
            cities.add(faker.address().cityName().replace("\"", ""));
        }
    }


    private static void generateBaseUsers() {
        // Generate users
        for (int i = 0; i < userCount; i++) {
            users.add(new User(faker.name().name().replace("\"", ""), faker.idNumber().valid(), getRandomCity(),
                    getRandomAge(), getRandomGender()));
        }

    }


    private static void generateBaseTags() {
        for (int i = 0; i < tagCount; i++) {
            tags.add(new Tag(faker.idNumber().valid(), tag_list.get(i), faker.friends().quote().replace("\"", "")));
        }
    }

    private static void generateBaseWeibos() {
        for (int i = 0; i < weiboCount; i++) {
            Weibo weibo = genNewWeibo();
            weibos.add(weibo);
        }
    }

    private static Weibo genNewWeibo() {
        return new Weibo(faker.idNumber().valid(), faker.friends().quote().replace("\"", ""), getRandomAgent(), getRandomUser());
    }


    private static void genSingleWeiboRelation(Weibo weibo) {
        if (currentSendNumber < 1) {
            return;
        }

        int tagsNum = Math.min(faker.number().numberBetween(0, currentSendNumber), tags.size() / 3);
        currentSendNumber -= tagsNum;
        generateTags(weibo, tagsNum);

        if (currentSendNumber < 1) {
            return;
        }

        int mentionedNum = Math.min(faker.number().numberBetween(0, currentSendNumber), weibos.size() / 3);
        currentSendNumber -= mentionedNum;
        generateMentionedWeibos(weibo.getMentionedWeibos(), weibo, mentionedNum);

        if (currentSendNumber < 1) {
            return;
        }
        int atUserNum = Math.min(faker.number().numberBetween(0, currentSendNumber), users.size() / 3);
        currentSendNumber -= atUserNum;
        generateRelationUsers(weibo.getAtUsers(), weibo.getAuthor(), atUserNum);

    }


    private static void genSingleCommentRelation(Comment comment) {
        if (currentSendNumber < 1)
            return;
        int mentionedNum = Math.min(faker.number().numberBetween(0, currentSendNumber), weibos.size() / 3);
        generateMentionedWeibos(comment.getMentionedWeibos(), comment.getReplyOf(), mentionedNum);
        currentSendNumber -= mentionedNum;

        if (currentSendNumber < 1)
            return;
        int atUserNum = Math.min(faker.number().numberBetween(0, currentSendNumber), users.size() / 3);
        generateRelationUsers(comment.getAtUsers(), comment.getAuthor(), atUserNum);
        currentSendNumber -= atUserNum;

    }

    private static void generateRelationUsers(ArrayList<User> hasRelatedUsers, User excludeUser, int maxNumber) {
        for (int j = 0; j < maxNumber; j++) {
            User newUser = getRandomUser();

            while (newUser.getId().equals(excludeUser.getId()) || hasRelatedUsers.contains(newUser)) {
                newUser = getRandomUser();
            }
            hasRelatedUsers.add(newUser);
        }
    }

    private static void generateMentionedWeibos(ArrayList<Weibo> hasMentionedWeibos, Weibo excludeWeibo, int maxNumber) {
        for (int j = 0; j < maxNumber; j++) {
            Weibo newMentionedWeibo = getRandomWeibo();
            while (newMentionedWeibo.getId().equals(excludeWeibo.getId()) || hasMentionedWeibos.contains(newMentionedWeibo)) {
                newMentionedWeibo = getRandomWeibo();
            }
            hasMentionedWeibos.add(newMentionedWeibo);
        }
    }

    private static void generateTags(Weibo weibo, int maxNumber) {
        ArrayList<Tag> weiboTags = weibo.getTags();
        for (int j = 0; j < maxNumber; j++) {
            Tag newTag = getRandomTag();
            while (weiboTags.contains(newTag)) {
                newTag = getRandomTag();
            }
            weiboTags.add(newTag);
        }

    }

    private static void generateBaseComments() {
        for (int i = 0; i < commentCount; i++) {
            comments.add(genNewComment());
        }
    }

    private static Comment genNewComment() {
        return new Comment(faker.idNumber().valid(), faker.twinPeaks().quote().replace("\"", ""), getRandomAgent(), getRandomUser(), getRandomWeibo());
    }

    private static String getRandomCity() {
        Random rand = new Random();
        int Index = rand.nextInt(cities.size());
        return cities.get(Index);
    }

    private static int getRandomAge() {
        return faker.number().numberBetween(18, 61);
    }

    private static String getRandomGender() {
        int index = faker.number().numberBetween(0, 2);

        return genders[index];
    }

    private static String getRandomAgent() {
        return agents.get(faker.number().numberBetween(0, agents.size()));
    }

    private static User getRandomUser() {
        return users.get(faker.number().numberBetween(0, users.size()));
    }

    private static Weibo getRandomWeibo() {
        return weibos.get(faker.number().numberBetween(0, weibos.size()));
    }

    private static Tag getRandomTag() {
        return tags.get(faker.number().numberBetween(0, tags.size()));
    }
}

