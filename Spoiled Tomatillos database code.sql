Drop database if exists SpoiledTomatillos;
CREATE database SpoiledTomatillos;
USE SpoiledTomatillos;

# this is a regular user
create table User (
	id int(10) primary key not null, 
    username varchar(20) unique not null, 
    user_type enum('regular', 'admin'),
    userpass varchar(20) not null
);


#truncate table Users; # truncate table <tableName> 清空table.

#create the actor table.
create table Actor (
	actor_id int(10) primary key not null, 
    actor_name varchar(20) not null
);

# create director table for all movies
create table Director (
	director_id int(10) primary key not null, 
    director_name varchar(20) not null
);

# create the movie table
create table Movie (
	movie_id int(10) primary key not null, 
    movie_name varchar(20) not null, 
    rating int(1) not null check(rating >= 0 and rating <= 10),
    genre varchar(10), 
    description varchar(700) not null
);

# this is the movie list -- revision needed

create table MovieList (
	user_id int(10) not null, 
    list_name varchar(30) not null, 
    movie_id int(10) not null, 
    primary key(user_id, list_name, movie_id),
    constraint to_user foreign key (user_id) references User(id), 
    constraint to_moive foreign key (movie_id) references Movie(movie_id)
);


# this is the actor list for a specific movie
create table ActorList (
    movie_id int(10) not null, 
    actor_id int(10) not null, 
    primary key(movie_id, actor_id),
    constraint to_movie foreign key (movie_id) references Movie(movie_id), 
    constraint to_actor foreign key (actor_id) references Actor(actor_id)
);


# this is the friend list
create table friendList (
	id int(20) primary key not null auto_increment,
	listOwner int(10) not null, 
    constraint whoseFriendList foreign key (listOwner) references User(id), 
    friendId int(10) not null, 
    friendStatus enum('friend', 'block')
);



# this is the director list for a specific movie
create table DirectorList (
    movie_id int(10) not null, 
    director_id int(10) not null, 
    primary key(movie_id, director_id),
    constraint direct_movie foreign key (movie_id) references Movie(movie_id), 
    constraint to_director foreign key (director_id) references Director(director_id)
);


# the review table for a movie 
create table Review (
	review_id int(10) primary key not null, 
    movie_id int(10) not null, 
    reviewer_id int(10) not null, 
    review_date date not null, 
    description varchar(500),
    rating int not null check(rating >= 0 and rating <= 5),
    constraint reviewer_reference foreign key (reviewer_id) references User(id),
    constraint movie_reference foreign key (movie_id) references Movie(movie_id)
);

#----------Insert the testing values into the database
insert into User values
(000001, "John", 'regular', "John123456"), 
(000002, "Alice", 'regular', "Alice123456"),
(000003, "Kyle", 'regular', "Kyle123456"), 
(000004, "Duke", 'admin', "Duke123456");

insert into Actor values
(000001, "Brandon"), 
(000002, "Lorry"), 
(000003, "Jack"),
(000004, "Johnson");

insert into Director values
(000001, "Bob"), 
(000002, "Clark"),
(000003, "Dannis");

insert into Movie values
(000001, "Tommrow Land", 2, "comedy", "this is a comedy movie"), 
(000002, "coach carter", 5, "sport", "this is a sport movie"), 
(000003, "Harry Potter 1", 4, "fiction", "this is a fiction movie"), 
(000004, "The advengers", 5, "science", "this is a science movie"), 
(000005, "Handsaw Ridge", 5, "war", "this is a war movie"), 
(000006, "Beaty and beast", 4, "drama", "this is a drama movie"), 
(000007, "The advengers 2", 5, "science", "this is a science movie"), 
(000008, "Harry Potter 2", 4, "fiction", "this is a fiction movie"), 
(000009, "Harry Potter 3", 4, "fiction", "this is a fiction movie"), 
(000010, "Funny story", 4, "comedy", "this is a comedy movie"), 
(000011, "Pacific War", 5, "war", "this is a war movie");

insert into MovieList values
(000001, "science movie", 000001), 
(000001, "science movie", 000007), 
(000002, "fiction movie", 000003), 
(000002, "fiction movie", 000008), 
(000003, "drama movie", 000006), 
(000004, "war movie", 000005), 
(000004, "war movie", 000011);

insert into friendList values
(1, 000001, 000002, 'friend'), 
(2, 000002, 000001, 'friend'),
(3, 000001, 000003, 'friend'),
(4, 000003, 000001, 'block'),
(5, 000001, 000004, 'friend'),
(6, 000003, 000002, 'friend'),
(7, 000002, 000003, 'friend'),
(8, 000004, 000002, 'friend'),
(9, 000002, 000004, 'block'),
(10, 000004, 000001, 'friend');

insert into ActorList values
(000001, 000001), (000001, 000002), 
(000001, 000003), (000002, 000001), 
(000002, 000002), (000003, 000003), 
(000003, 000002), (000004, 000001), 
(000004, 000004), (000005, 000002), 
(000005, 000004), (000006, 000003), 
(000007, 000004), (000007, 000002), 
(000008, 000003), (000008, 000002), 
(000008, 000001), (000009, 000004), 
(000009, 000001), (000009, 000002), 
(000009, 000003), (000010, 000004), 
(000010, 000002), (000011, 000004);

insert into DirectorList values
(000001, 000001), (000001, 000002),
(000001, 000003), (000002, 000001),
(000002, 000003), (000003, 000001),
(000004, 000001), (000004, 000002);

insert into Review values
(000001, 000001, 000001, '2017-01-01', "This is not a pretty good movie though, waste of my time", 3),
(000002, 000001, 000002, '2017-01-01', "This is not a pretty good movie though, waste of my time", 2),
(000003, 000001, 000003, '2017-01-01', "This is not a pretty good movie though, waste of my time", 1),
(000004, 000002, 000003, '2017-10-01', "This is a pretty good movie", 5),
(000005, 000002, 000002, '2017-11-01', "This is a pretty good movie", 5),
(000006, 000002, 000001, '2017-01-01', "This is a pretty good movie", 5),
(000007, 000002, 000004, '2017-01-01', "This is a pretty good movie", 5),
(000008, 000005, 000001, '2018-01-01', "This is a pretty good war movie", 5),
(000009, 000011, 000003, '2018-02-01', "This is a an awsome movie", 5),
(000010, 000010, 000002, '2018-01-01', "This is a funny movie", 4),
(000011, 000003, 000001, '2018-01-01', "Harry potter, the classic magic movie, from John", 5),
(000012, 000003, 000002, '2018-01-01', "Harry potter, the classic magic movie, from Alice", 5),
(000013, 000008, 000003, '2018-01-01', "Harry potter, the classic magic movie from Kyle", 5),
(000014, 000009, 000004, '2018-01-01', "Harry potter, the classic magic movie from Duke", 5);



#---------------------test functionality------------
#first get all friends from 000001 users
