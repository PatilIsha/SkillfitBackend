package com.example.config;

import com.example.model.Job;
import com.example.model.QuestionEntity;
import com.example.repository.JobRepository;
import com.example.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Override
    public void run(String... args) throws Exception {
        // Only initialize if database is empty
        if (jobRepository.count() == 0) {
            List<Job> sampleJobs = Arrays.asList(
                // Beginner Level Jobs
                createJob("Frontend Developer (React)", "Beginner", 
                    "Build responsive web applications using React.js. Work with HTML, CSS, and JavaScript. Learn component-based architecture and state management. Perfect for those starting their frontend journey."),
                
                createJob("Backend Developer (Java)", "Beginner", 
                    "Develop server-side applications using Java and Spring Boot. Learn RESTful API development, database integration, and basic server architecture. Ideal for beginners interested in backend development."),
                
                createJob("Full Stack Developer (MERN)", "Beginner", 
                    "Work with MongoDB, Express.js, React, and Node.js to build complete web applications. Learn both frontend and backend development in a modern JavaScript stack."),
                
                createJob("Python Developer", "Beginner", 
                    "Develop applications using Python. Learn web frameworks like Flask or Django, work with databases, and build REST APIs. Great entry point for software development."),
                
                createJob("UI/UX Developer", "Beginner", 
                    "Create beautiful and user-friendly interfaces. Work with design tools, HTML, CSS, and JavaScript. Focus on user experience and responsive design principles."),
                
                // Intermediate Level Jobs
                createJob("Senior Frontend Developer (React/TypeScript)", "Intermediate", 
                    "Lead frontend development using React and TypeScript. Implement advanced state management with Redux, optimize performance, and mentor junior developers. Requires 2+ years of experience."),
                
                createJob("Backend Engineer (Spring Boot)", "Intermediate", 
                    "Design and develop scalable backend systems using Spring Boot and Java. Implement microservices architecture, work with databases (MySQL, PostgreSQL), and ensure system reliability."),
                
                createJob("Full Stack Engineer (Node.js)", "Intermediate", 
                    "Build end-to-end applications using Node.js, Express, React, and MongoDB. Implement authentication, real-time features, and deploy applications to cloud platforms."),
                
                createJob("DevOps Engineer", "Intermediate", 
                    "Manage CI/CD pipelines, containerization with Docker, cloud infrastructure (AWS/Azure), and monitoring systems. Automate deployment processes and ensure system scalability."),
                
                createJob("Mobile App Developer (React Native)", "Intermediate", 
                    "Develop cross-platform mobile applications using React Native. Build iOS and Android apps with a single codebase, implement native features, and optimize app performance."),
                
                createJob("Database Developer", "Intermediate", 
                    "Design and optimize database schemas, write complex SQL queries, implement data migration strategies, and ensure data integrity. Work with MySQL, PostgreSQL, or MongoDB."),
                
                createJob("API Developer", "Intermediate", 
                    "Design and develop RESTful and GraphQL APIs. Implement authentication, rate limiting, documentation, and ensure API security and performance. Work with various backend frameworks."),
                
                // Expert Level Jobs
                createJob("Lead Software Architect", "Expert", 
                    "Design system architecture for large-scale applications. Make technical decisions, lead development teams, and ensure scalability, security, and maintainability. Requires 5+ years of experience."),
                
                createJob("Senior Full Stack Engineer", "Expert", 
                    "Lead full-stack development projects. Architect complex applications, mentor teams, implement best practices, and drive technical innovation. Expertise in multiple technologies required."),
                
                createJob("Cloud Solutions Architect", "Expert", 
                    "Design and implement cloud-native solutions on AWS, Azure, or GCP. Architect microservices, implement DevOps practices, and ensure high availability and scalability."),
                
                createJob("Senior Backend Engineer (Distributed Systems)", "Expert", 
                    "Design and build distributed systems, microservices architecture, and high-performance backend systems. Work with message queues, caching strategies, and ensure system reliability at scale."),
                
                createJob("Tech Lead / Engineering Manager", "Expert", 
                    "Lead engineering teams, make architectural decisions, drive technical strategy, and mentor developers. Balance technical excellence with business objectives. Requires strong leadership skills."),
                
                createJob("Senior Frontend Architect", "Expert", 
                    "Architect large-scale frontend applications, establish coding standards, implement advanced patterns, and optimize for performance. Lead frontend teams and drive technical innovation."),
                
                createJob("Security Engineer", "Expert", 
                    "Implement security best practices, conduct security audits, protect against vulnerabilities, and ensure compliance. Expertise in OWASP, encryption, and secure coding practices required."),
                
                createJob("Machine Learning Engineer", "Expert", 
                    "Develop and deploy machine learning models, work with data pipelines, implement ML algorithms, and integrate AI solutions into production systems. Strong Python and ML framework expertise required.")
            );

            jobRepository.saveAll(sampleJobs);
            System.out.println("✅ Initialized " + sampleJobs.size() + " sample jobs in the database.");
        }

        // Initialize sample test questions if database is empty
        if (questionRepository.count() == 0) {
            List<QuestionEntity> sampleQuestions = Arrays.asList(
                createQuestion(
                    "Which language is used for backend development?",
                    Arrays.asList("HTML", "Java", "CSS", "Bootstrap"),
                    "Java"
                ),
                createQuestion(
                    "React is a ______ library?",
                    Arrays.asList("Backend", "Frontend", "Database", "Testing"),
                    "Frontend"
                ),
                createQuestion(
                    "Spring Boot is written in which language?",
                    Arrays.asList("Python", "C#", "Java", "Kotlin"),
                    "Java"
                ),
                createQuestion(
                    "Which database is NoSQL?",
                    Arrays.asList("MySQL", "MongoDB", "Oracle", "PostgreSQL"),
                    "MongoDB"
                ),
                createQuestion(
                    "What does REST stand for in RESTful API?",
                    Arrays.asList("Representational State Transfer", "Remote State Transfer", "Resource State Transfer", "Representational Server Transfer"),
                    "Representational State Transfer"
                ),
                createQuestion(
                    "Which HTTP method is used to create a new resource?",
                    Arrays.asList("GET", "POST", "PUT", "DELETE"),
                    "POST"
                ),
                createQuestion(
                    "What is the main purpose of useEffect hook in React?",
                    Arrays.asList("To manage component state", "To perform side effects", "To render components", "To handle events"),
                    "To perform side effects"
                ),
                createQuestion(
                    "Which of the following is a JavaScript framework?",
                    Arrays.asList("Django", "Flask", "Angular", "Spring"),
                    "Angular"
                ),
                createQuestion(
                    "What is the default port for a Spring Boot application?",
                    Arrays.asList("3000", "8080", "5000", "8000"),
                    "8080"
                ),
                createQuestion(
                    "Which annotation is used to mark a class as a Spring component?",
                    Arrays.asList("@Service", "@Component", "@Repository", "@Controller"),
                    "@Component"
                ),
                createQuestion(
                    "What is the purpose of @Autowired annotation in Spring?",
                    Arrays.asList("To create a new bean", "To inject dependencies", "To configure the application", "To handle HTTP requests"),
                    "To inject dependencies"
                ),
                createQuestion(
                    "Which method is used to update state in React functional components?",
                    Arrays.asList("setState()", "useState()", "updateState()", "changeState()"),
                    "useState()"
                ),
                createQuestion(
                    "What does SQL stand for?",
                    Arrays.asList("Structured Query Language", "Simple Query Language", "Standard Query Language", "System Query Language"),
                    "Structured Query Language"
                ),
                createQuestion(
                    "Which of the following is a relational database?",
                    Arrays.asList("MongoDB", "Redis", "MySQL", "Elasticsearch"),
                    "MySQL"
                ),
                createQuestion(
                    "What is the purpose of Git?",
                    Arrays.asList("To write code", "To manage databases", "To track changes in code", "To deploy applications"),
                    "To track changes in code"
                ),
                createQuestion(
                    "Which command is used to stage files in Git?",
                    Arrays.asList("git commit", "git add", "git push", "git pull"),
                    "git add"
                ),
                createQuestion(
                    "What is the purpose of Docker?",
                    Arrays.asList("To write code", "To containerize applications", "To manage databases", "To design UI"),
                    "To containerize applications"
                ),
                createQuestion(
                    "Which of the following is a cloud service provider?",
                    Arrays.asList("GitHub", "Docker", "AWS", "Node.js"),
                    "AWS"
                ),
                createQuestion(
                    "What is the main purpose of npm in Node.js?",
                    Arrays.asList("To run JavaScript", "To manage packages", "To create servers", "To handle databases"),
                    "To manage packages"
                ),
                createQuestion(
                    "Which HTTP status code indicates success?",
                    Arrays.asList("200", "404", "500", "401"),
                    "200"
                )
            );

            questionRepository.saveAll(sampleQuestions);
            System.out.println("✅ Initialized " + sampleQuestions.size() + " sample test questions in the database.");
        }
    }

    private Job createJob(String role, String level, String description) {
        Job job = new Job();
        job.setRole(role);
        job.setLevel(level);
        job.setDescription(description);
        return job;
    }

    private QuestionEntity createQuestion(String questionText, List<String> options, String correctAnswer) {
        QuestionEntity question = new QuestionEntity();
        question.setQuestionText(questionText);
        question.setOptions(options);
        question.setCorrectAnswer(correctAnswer);
        question.setActive(true);
        return question;
    }
}

