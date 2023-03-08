package com.nithin.gradlejwttokens.AuthenticationService.MailConfig;

import com.itextpdf.text.Document;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nithin.gradlejwttokens.AuthenticationService.Model.Role;
import com.nithin.gradlejwttokens.AuthenticationService.Model.User;
import com.nithin.gradlejwttokens.AuthenticationService.repository.UserRepository;
import com.nithin.gradlejwttokens.AuthenticationService.service.UserService;
import com.opencsv.CSVReader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class FileUtils {
    private final UserService userService;
    private final UserRepository userRepository;

    public FileUtils(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public void importPdf(MultipartFile file, List<String> missingIds, List<String> usernameList, List<String> emailList,
                          List<String> passwordList, List<String> roleList, List<String> idsToUpdate) throws Exception {
        if (file.isEmpty()) {
            throw new Exception("File is empty");
        }

        PDDocument document = PDDocument.load(file.getInputStream());
        PDFTextStripper pdfStripper = new PDFTextStripper();

        String[] lines = pdfStripper.getText(document).split("\\r?\\n");
        int row = 0;
        boolean isFirstRow = true;
        for (String line : lines) {
            if (isFirstRow) {
                isFirstRow = false;
                continue;
            }
            String[] parts = line.split("\\s+(?!\\n)");
            if (parts.length == 5) {

                String id = parts[0];
                String username = parts[1];
                String email = parts[2];
                String password = parts[3];
                String role = parts[4];

                for (String pdfId : missingIds) {
                    if (pdfId.equals(id)){
                        User user = new User();
                        user.setId(id);
                        user.setUsername(username);
                        user.setEmail(email);
                        user.setPassword(password);
                        user.setRole(Role.valueOf(role));
                        userService.saveUser(user);
                    }


                }

                if (idsToUpdate.size() > 0) {
                    for (String idsToUpdate1 : idsToUpdate) {
                        if (idsToUpdate1.equals(id)){
                            User user = userRepository.findById(id).orElse(null);
                            if (user != null) {
                                if (usernameList.contains(id)) {
                                    user.setUsername(username);
                                }
                                if (emailList.contains(id)) {
                                    user.setEmail(email);
                                }
                                if (passwordList.contains(id)) {
                                    user.setPassword(password);
                                }
                                if (roleList.contains(id)) {
                                    user.setRole(Role.valueOf(role));
                                }
                                userService.saveUser(user);
                            }
                        }

                    }
                }

                row++;
            }

        }
        System.out.println("Row:" + row);
        document.close();

    }

    public void importCsv(MultipartFile file, List<String> missingIds, List<String> usernameList, List<String> emailList,
                          List<String> passwordList, List<String> roleList, List<String> idsToUpdate) throws Exception {
        if (file.isEmpty()) {
            throw new Exception("File is empty");
        }

        CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()));
        int row = 0;
        String[] line;
        boolean isFirstRow = true;
        while ((line = reader.readNext()) != null) {
            if (isFirstRow) {
                isFirstRow = false;
                continue;
            }
            String id = line[1];
            String username = line[4];
            String email = line[0];
            String password = line[2];
            Role role = Role.valueOf(line[3]);

                for (String csvId : missingIds) {
                    if (csvId.equals(id)){
                        User user = new User();
                        user.setId(id);
                        user.setUsername(username);
                        user.setEmail(email);
                        user.setPassword(password);
                        user.setRole(role);
                        userService.saveUser(user);
                    }
                }

                if (idsToUpdate.size() > 0) {
                    for (String idsToUpdate1 : idsToUpdate) {
                        if (idsToUpdate1.equals(id)){
                            User user = userRepository.findById(id).orElse(null);
                            if (user != null) {
                                if (usernameList.contains(id)) {
                                    user.setUsername(username);
                                }
                                if (emailList.contains(id)) {
                                    user.setEmail(email);
                                }
                                if (passwordList.contains(id)) {
                                    user.setPassword(password);
                                }
                                if (roleList.contains(id)) {
                                    user.setRole(role);
                                }
                                userService.saveUser(user);
                            }
                        }
                    }
                }

                row++;

        }
        System.out.println("Row:" + row);
        reader.close();
    }





    public void generatePdf(List<User> users, OutputStream outputStream) throws Exception {
        // Creating the Object of Document
        Document document = new Document(PageSize.A3);
        // Getting instance of PdfWriter
        PdfWriter.getInstance(document, outputStream);
        // Opening the created document to change it
        document.open();
        // Creating font
        // Setting font style and size
        /*com.itextpdf.text.Font fontTitle = FontFactory.getFont(FontFactory.TIMES_ROMAN);
        fontTitle.setSize(20);
        // Creating paragraph
        Paragraph paragraph1 = new Paragraph("List of the Users", fontTitle);
        // Aligning the paragraph in the document
        paragraph1.setAlignment(Paragraph.ALIGN_CENTER);
        // Adding the created paragraph in the document
        document.add(paragraph1);*/
        // Creating a table of the 4 columns
        PdfPTable table = new PdfPTable(5);
        // Setting width of the table, its columns and spacing
        table.setWidths(new int[]{6, 4, 6, 4, 2});
        //table.setSpacingBefore(5);
        // Create Table Cells for the table header
        PdfPCell cell = new PdfPCell();
        // Setting the background color and padding of the table cell
        cell.setBackgroundColor(CMYKColor.WHITE);
        cell.setPadding(4);
        // Creating font
        // Setting font style and size
        com.itextpdf.text.Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN);
        font.setColor(CMYKColor.BLACK);
        // Adding headings in the created table cell or  header
        // Adding Cell to table
        cell.setPhrase(new Phrase("ID", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Username", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Email", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Password", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Role", font));
        table.addCell(cell);
        // Iterating the list of students
        for (User user : users) {
            // Adding user id
            PdfPCell cell1 = new PdfPCell(new Phrase(String.valueOf(user.getId()), font));
            table.addCell(cell1);
            // Adding username
            PdfPCell cell2 = new PdfPCell(new Phrase(user.getUsername(), font));
            table.addCell(cell2);
            // Adding user email
            PdfPCell cell3 = new PdfPCell(new Phrase(user.getEmail(), font));
            table.addCell(cell3);
            // Adding user password
            PdfPCell cell4 = new PdfPCell(new Phrase(user.getPassword(), font));
            table.addCell(cell4);
            // Adding user role
            PdfPCell cell5 = new PdfPCell(new Phrase(String.valueOf(user.getRole()), font));
            table.addCell(cell5);
        }
        // Adding the created table to the document
        document.add(table);
        // Closing the document
        document.close();
    }

    public List<String> validatePdf(MultipartFile file, List<User> users, boolean importData) throws Exception {
        if (file.isEmpty()) {
            throw new Exception("File is empty.");
        }

        List<String> result = new ArrayList<>();
        PDDocument document = PDDocument.load(file.getInputStream());
        PDFTextStripper pdfStripper = new PDFTextStripper();

        String[] lines = pdfStripper.getText(document).split("\\r?\\n");
        int row = 0;
        int usernameCount = 0;
        List<String> usernameList = new ArrayList<>();
        int emailCount = 0;
        List<String> emailList = new ArrayList<>();
        int passwordCount = 0;
        List<String> passwordList = new ArrayList<>();
        int roleCount = 0;
        List<String> roleList = new ArrayList<>();
        Set<String > idsToUpdate = new HashSet<>();
        Set<String> dbUserIds = new HashSet<>();
        Set<String> pdfUserIds = new HashSet<>();

        for (User dbuser : users) {
            dbUserIds.add(dbuser.getId());

            boolean isFirstRow = true;
            for (String line : lines) {
                if (isFirstRow) {
                    isFirstRow = false;
                    continue;
                }
                String[] parts = line.split("\\s+(?!\\n)");
                if (parts.length == 5) {

                    String id = parts[0];
                    String username = parts[1];
                    String email = parts[2];
                    String password = parts[3];
                    Role role = Role.valueOf(parts[4]);

                    pdfUserIds.add(id);

                    if (id.equals(dbuser.getId())) {

                        if (!username.equals(dbuser.getUsername())) {
                            row++;
                            usernameCount++;
                            usernameList.add(dbuser.getId());
                            idsToUpdate.add(dbuser.getId());
                        }
                        if (!email.equals(dbuser.getEmail())) {
                            row++;
                            emailCount++;
                            emailList.add(dbuser.getId());
                            idsToUpdate.add(dbuser.getId());
                        }
                        if (!password.equals(dbuser.getPassword())){
                            row++;
                            passwordCount++;
                            passwordList.add(dbuser.getId());
                            idsToUpdate.add(dbuser.getId());
                        }
                        if (!role.equals(dbuser.getRole())){
                            row++;
                            roleCount++;
                            roleList.add(dbuser.getId());
                            idsToUpdate.add(dbuser.getId());
                        }
                    }

                }
            }
        }
        Set<String> missingIds = new HashSet<>(pdfUserIds);
        missingIds.removeAll(dbUserIds);
        Set<String> toDeleteIds = new HashSet<>(dbUserIds);
        toDeleteIds.removeAll(pdfUserIds);
            if (missingIds.size()==0) {
                result.add("Creation: 0");
            }
            else {
                result.add("Creation: "+ missingIds.size());
                result.add("Missing User Ids");
                result.addAll(missingIds);
            }

            int update = usernameCount+emailCount+passwordCount+roleCount;
            if(update>0){
                result.add("Update:"+update);
                result.add("Username: " + usernameCount);
                result.addAll(usernameList);
                result.add("Email: " + emailCount);
                result.addAll(emailList);
                result.add("Password: " + passwordCount);
                result.addAll(passwordList);
                result.add("Role: " + roleCount);
                result.addAll(roleList);
            }
            else {
                result.add("Update:"+update);
            }
            if (toDeleteIds.size()==0){
                result.add("Deletion: 0");
            }
            else {
                result.add("Deletion: "+ toDeleteIds.size());
                result.add("Ids to delete:");
                result.addAll(missingIds);
            }

        List<String> missingIdsList = new ArrayList<>(missingIds);
        List<String> idsToUpdateList = new ArrayList<>(idsToUpdate);
        if (importData){
            importPdf(file, missingIdsList, usernameList, emailList, passwordList, roleList, idsToUpdateList);
        }

        System.out.println("Row:" + row);
        document.close();
        return result;
    }

    public List<String> validateCsv(MultipartFile file, List<User> users, boolean importData) throws Exception {
        if (file.isEmpty()) {
            throw new Exception("File is empty.");
        }

        List<String> result = new ArrayList<>();

        CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()));
        int row = 1; // start from row 2

        int usernameCount = 0;
        List<String> usernameList = new ArrayList<>();
        int emailCount = 0;
        List<String> emailList = new ArrayList<>();
        int passwordCount = 0;
        List<String> passwordList = new ArrayList<>();
        int roleCount = 0;
        List<String> roleList = new ArrayList<>();
        Set<String> idsToUpdate = new HashSet<>();
        Set<String> dbUserIds = new HashSet<>();
        Set<String> csvUserIds = new HashSet<>();

        for (User dbUser : users) {
            dbUserIds.add(dbUser.getId());
        }

        String[] line;
        boolean isFirstRow = true;
        while ((line = reader.readNext()) != null) {
            if (isFirstRow) {
                isFirstRow = false;
                continue;
            }
            String id = line[1];
            String username = line[4];
            String email = line[0];
            String password = line[2];
            Role role = Role.valueOf(line[3]);
            csvUserIds.add(id);

            if (dbUserIds.contains(id)) {
                User dbUser = userService.getUserById(id);
                if (!username.equals(dbUser.getUsername())) {
                    row++;
                    usernameCount++;
                    usernameList.add(id);
                    idsToUpdate.add(id);
                }
                if (!email.equals(dbUser.getEmail())) {
                    row++;
                    emailCount++;
                    emailList.add(id);
                    idsToUpdate.add(id);
                }
                if (!password.equals(dbUser.getPassword())) {
                    row++;
                    passwordCount++;
                    passwordList.add(id);
                    idsToUpdate.add(id);
                }
                if (!role.equals(dbUser.getRole())) {
                    row++;
                    roleCount++;
                    roleList.add(id);
                    idsToUpdate.add(id);
                }
            }
        }

        Set<String> missingIds = new HashSet<>(csvUserIds);
        missingIds.removeAll(dbUserIds);
        Set<String> toDeleteIds = new HashSet<>(dbUserIds);
        toDeleteIds.removeAll(csvUserIds);

        if (!missingIds.isEmpty()) {
            int missingIdsCount = missingIds.size();
            result.add(String.format("Creation: %d", missingIdsCount));
            result.add("Missing User Ids");
            result.addAll(missingIds);
        } else {
            result.add("Creation: 0");
        }

        int updateCount = usernameCount + emailCount + passwordCount + roleCount;
        if (updateCount > 0) {
            result.add(String.format("Update: %d", updateCount));
            result.add(String.format("Username: %d", usernameCount));
            result.addAll(usernameList);
            result.add(String.format("Email: %d", emailCount));
            result.addAll(emailList);
            result.add(String.format("Password: %d", passwordCount));
            result.addAll(passwordList);
            result.add(String.format("Role: %d", roleCount));
            result.addAll(roleList);
        } else {
            result.add("Update: 0");
        }
        if (toDeleteIds.size()==0){
            result.add("Deletion: 0");
        }
        else {
            result.add("Deletion: "+ toDeleteIds.size());
            result.add("Ids to delete:");
            result.addAll(missingIds);
        }

        List<String> missingIdsList = new ArrayList<>(missingIds);
        List<String> idsToUpdateList = new ArrayList<>(idsToUpdate);
        if (importData) {
            importCsv(file, missingIdsList, usernameList, emailList, passwordList, roleList, idsToUpdateList);
        }

        reader.close();

        return result;
    }

}


