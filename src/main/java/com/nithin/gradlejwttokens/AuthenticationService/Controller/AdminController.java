package com.nithin.gradlejwttokens.AuthenticationService.Controller;

import com.nithin.gradlejwttokens.AuthenticationService.MailConfig.FileUtils;
import com.nithin.gradlejwttokens.AuthenticationService.MailConfig.MailConfiguration;
import com.nithin.gradlejwttokens.AuthenticationService.MailConfig.UserListWrapper;
import com.nithin.gradlejwttokens.AuthenticationService.Model.Role;
import com.nithin.gradlejwttokens.AuthenticationService.Model.User;
import com.nithin.gradlejwttokens.AuthenticationService.exception.UserNotFoundException;
import com.nithin.gradlejwttokens.AuthenticationService.service.UserService;
import com.opencsv.CSVReader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Objects;

import static com.nithin.gradlejwttokens.AuthenticationService.config.PasswordUtils.encodePassword;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final UserService userService;
    private final MailConfiguration mailConfiguration;
    private final FileUtils fileUtils;

    public AdminController( UserService userService, MailConfiguration mailConfiguration, FileUtils fileUtils) {
        this.userService = userService;
        this.mailConfiguration = mailConfiguration;
        this.fileUtils = fileUtils;
    }

    @PutMapping(path = "/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody User user)
            throws UserNotFoundException {
//        String name = WordUtils.capitalizeFully(username);
        User userData = userService.getUserById(id);

            userData.setUsername(user.getUsername());
            userData.setEmail(user.getEmail());
            String password = encodePassword(user.getPassword());
            userData.setPassword(password);
            userData.setRole(user.getRole());

            userService.saveUser(userData);

            return ResponseEntity.ok(userData);
    }

    @GetMapping(path = "/export" ,produces = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity exportEndpoint(@RequestParam(required = false, name = "id") String id,
                                         @RequestParam("mode") String mode) throws Exception {
        List<User> users = userService.listAll();
        User userData = userService.getUserById(id);
        switch (mode.toLowerCase()) {
            case "json":
                return ResponseEntity.ok(users);
            case "xml":
                UserListWrapper wrapper = new UserListWrapper(users);

                StringWriter writer = new StringWriter();
                JAXBContext context = JAXBContext.newInstance(UserListWrapper.class);
                Marshaller m = context.createMarshaller();
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                m.marshal(wrapper, writer);
                String xml = writer.toString();
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(xml);
            case "pdf":
                if (id == null){
                    List<User> userList = userService.listAllAdmins();
                    String[] emails = userList.stream().map(User::getEmail).toArray(String[]::new);
                    mailConfiguration.emailPdfToAllAdmins(userService.listAll(),emails, "PDF ",
                            "Hello! This is the pdf containing the list of existing users.");
                    String message = "An email containing all the existing users has been sent to all the admins.";
                    return ResponseEntity.ok().body(message);
                }
                else {
                    if (userData.getRole()== Role.ADMIN){
                    mailConfiguration.emailPdf(userService.listAll(), userData.getEmail(), "PDF ",
                            "Hello " + userData.getUsername() +
                                    ". This is the pdf containing the list of existing users.");
                    return ResponseEntity.ok().body(userService.getUserById(id));
                    }
                    else {
                        return ResponseEntity.badRequest().body("Can send email only to Admins.");
                    }
                }
            case "csv":
                if (id == null){
                    List<User> userList = userService.listAllAdmins();
                    String[] emails = userList.stream().map(User::getEmail).toArray(String[]::new);
                    mailConfiguration.emailCsvToAllAdmins(userService.listAll(),emails, "CSV ",
                            "Hello! This is the csv containing the list of existing users.");
                    String message = "An email containing all the existing users has been sent to all the admins.";
                    return ResponseEntity.ok().body(message);
                }
                else {
                    if (userData.getRole()== Role.ADMIN){
                        mailConfiguration.emailCsv(users, userData.getEmail(), "CSV",
                                "Hello " + userData.getUsername() +
                                        ". This is the csv containing the list of existing users.");
                        return ResponseEntity.ok().body(userService.getUserById(id));
                    }
                    else {
                        return ResponseEntity.badRequest().body("Can send email only to Admins.");
                    }
                }


            default:
                return ResponseEntity.badRequest().body("Invalid mode parameter");
        }

    }

    @PostMapping(path = "/import")
    public ResponseEntity<String> uploadPdf(@RequestParam("file") MultipartFile file, @RequestParam("mode") String mode) {
        List<User> users = userService.listAll();
        try {
            switch (mode.toLowerCase()) {
                case "pdf":
                    // Check if the uploaded file is a PDF
                    if (!Objects.equals(file.getContentType(), "application/pdf")) {
                        return ResponseEntity.badRequest().body("File must be a PDF");
                    }
                    fileUtils.validatePdf(file,users, true);

                    return ResponseEntity.ok("Users created in table successfully.");
                case "csv":
                    // Check if the uploaded file is a CSV
                    if (!Objects.equals(file.getContentType(), "text/csv")) {
                        return ResponseEntity.badRequest().body("File must be a CSV");
                    }
                    // Get the input stream of the uploaded file
                    InputStream inputStream = file.getInputStream();

                    // Create a CSV reader
                    CSVReader reader = new CSVReader(new InputStreamReader(inputStream));
                    fileUtils.validateCsv(file,users, true);

                    return ResponseEntity.ok("Users created in table successfully.");


                default:
                    return ResponseEntity.badRequest().body("Invalid mode");
            }


        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to upload PDF");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(path = "/validate")
    public ResponseEntity validate(@RequestParam("file") MultipartFile file, @RequestParam("mode")String mode) throws Exception {
        List<User> users = userService.listAll();
        switch (mode.toLowerCase()) {
            case "pdf":
                if (!Objects.equals(file.getContentType(), "application/pdf")) {
                    return ResponseEntity.badRequest().body("File must be a PDF");
                }
                List<String> result = fileUtils.validatePdf(file, users, false);
                return ResponseEntity.ok().body(result);
            case "csv":
                if (!Objects.equals(file.getContentType(), "text/csv")) {
                    return ResponseEntity.badRequest().body("File must be a CSV");
                }

                // Get the input stream of the uploaded file
                InputStream inputStream = file.getInputStream();

                // Create a CSV reader
                CSVReader reader = new CSVReader(new InputStreamReader(inputStream));
                List<String> result1 = fileUtils.validateCsv(file, users, false);
                return ResponseEntity.ok().body(result1);
            default:
                return ResponseEntity.badRequest().body("Invalid mode parameter");
        }

    }
}