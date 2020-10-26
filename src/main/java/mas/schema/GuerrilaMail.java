package mas.schema;

public class GuerrilaMail {


    private String result;
    private String mailFrom;
    private String mailSubject;
    private String mailBody;
    private long mail_timestamp;
    private String mailDate;
    private String mailId;
    private String e_mail;
    private String sessionId;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMailFrom() {
        return mailFrom;
    }

    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }

    public String getMailSubject() {
        return mailSubject;
    }

    public void setMailSubject(String mail_subject) {
        this.mailSubject = mail_subject;
    }

    public String getMailBody() {
        return mailBody;
    }

    public void setMailBody(String mailBody) {
        this.mailBody = mailBody;
    }

    public long getMail_timestamp() {
        return mail_timestamp;
    }

    public void setMailTimestamp(long mail_timestamp) {
        this.mail_timestamp = mail_timestamp;
    }

    public String getMailDate() {
        return mailDate;
    }

    public void setMailDate(String mail_date) {
        this.mailDate = mail_date;
    }

    public String getMailId() {
        return mailId;
    }

    public void setMailId(String mail_id) {
        this.mailId = mail_id;
    }

    public String getEmail() {
        return e_mail;
    }

    public void setEmail(String e_mail) {
        this.e_mail = e_mail;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
