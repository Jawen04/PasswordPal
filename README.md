# An easy password manager

## Description
A traditional password manager that lets the user:
- Generate new random passwords, with tweaks for security and comfort
- Store and manage passwords for different services
- Check overall security score for passwords
- Change profile settings


## Usage

The program contains of three parts: 
1. The frontend, written in Javascript react
2. A backend server, using java and the springboot framework for managing requests to the server
3. A database managed by MySQL


# Starting the fullstack application

## Using Docker 
If you have Docker installed on your machine you can start the application easily with these steps:

1. cd to the project's root directory
2. Make sure you have no actively running containers that can potentially block ports by running the command in section 'cleaning docker'
3. Build the docker images by running 
```bash 
docker compose build
4. Start the application (in detached mode) by typing 
```bash 
docker compose up -d
```

## Optional:
- view running containers: 
```bash 
docker compose ps
```
- check logs: 
```bash 
docker compose logs -f
```
- for more information on what you can do with Docker commands, visit Docker docs: 'https://docs.docker.com/reference/cli/docker/'

The frontend application is now visible at 'http://localhost'
The Backend application (although not much info) is visible at 'http://localhost:8080'


## Starting everything manually (tidious)
### Starting the database 
Make sure you have MySQL installed on your machine and run the following commands:

```bash
mysql start
mysql -u root -p                            # login as root, enter root password
CREATE DATABASE adminUsersDB                # Create the database 
USE adminUsersDB

CREATE TABLE users (                        # Create the table where credentials are stored         
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

```

# Starting the backend server

1. CD to the directory called 'backend' 
2. run 
```bash 
./gradlew run 
```


### Starting the frontend server
The frontend is using the NodeJS runtime environment so make sure you have NodeJS installed 

1. CD to the directory called frontend
2. First install all dependencies
```bash 
npm install
```
2. then run to start the live server
```bash 
npm start
```
3. the application is now visible on the URL: 'http://localhost:5173'


## Contributing

Contributions are welcome and appreciated!

If you have an idea, bug fix, improvement, or new feature you'd like to add, feel free to get involved.

### How to Contribute

1. **Fork** the repository.
2. **Clone** your fork to your local machine:
   ```bash
   git clone https://github.com/your-username/repo-name.git
   ```
3. Create a new branch for your feature or bug fix:
```bash 
git checkout -b feature/your-feature-name
```
4. Make your changes and commit them with clear messages:
```bash 
git commit -m "Add feature XYZ"
```
5. Push your changes to your fork:
```bash 
git push origin feature/your-feature-name
```
6. Open a Pull Request on the main repository and describe your changes.