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

#----------Insert the testing values into the database-------------


#---------------------test functionality------------

