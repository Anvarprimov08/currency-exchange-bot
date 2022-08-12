import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.Currency;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


public class Main extends TelegramLongPollingBot {
    private static ArrayList<Currency> currencies = null;
    private static String C_DOLLAR = "AQSH dollari";
    private static String C_EURO = "EVRO";
    private static String C_RUBLE = "Rossiya rubli";
    private static String C_YUAN = "Xitoy yuani";
    private static String C_CHOSEN_CURRENCY = null;
    private static String C_OPERATION = null;
    private static String C_LANG = "uz";
    private static String C_SUM = "so'm";
    public static void main(String[] args) {
         Gson gson = new GsonBuilder().setPrettyPrinting().create();


        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(new Main());

            URL url = new URL("https://cbu.uz/oz/arkhiv-kursov-valyut/json/");
            URLConnection urlConnection = url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(inputStreamReader);

            Type type = new TypeToken<ArrayList<model.Currency>>(){}.getType();
            currencies = gson.fromJson(br, type);

//            currencies.forEach(c -> System.out.println(c.getCcyNm_UZ()+" "+c.getCcyNm_EN()+" "+c.getCcyNm_RU()));

        } catch (TelegramApiException | IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getBotUsername() {
        return "username of your bot";
    }

    @Override
    public String getBotToken() {
        return "your bot token here";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()){
            Message message = update.getMessage();
            if (message.hasText()){
                String text = message.getText();
                if (text.equals("/start")){
                    setLanguage("markaziy bank rasmiy botiga xush kelibsiz\nTilni tanlang", message);
                }
                else if (text.equals("/language")){
                    if (C_LANG.equals("en")){
                        setLanguage("Select a language", message);
                    }
                    else if (C_LANG.equals("ru")){
                        setLanguage("Выберите язык", message);
                    }
                    else {
                        setLanguage("Tilni tanlang", message);
                    }
                }
                else if (text.equals(C_DOLLAR)){
                    C_CHOSEN_CURRENCY = C_DOLLAR;
                    inlineKeyboardMakerForCurrency(message);
                }
                else if (text.equals(C_EURO)){
                    C_CHOSEN_CURRENCY = C_EURO;
                    inlineKeyboardMakerForCurrency(message);
                }
                else if (text.equals(C_YUAN)){
                    C_CHOSEN_CURRENCY = C_YUAN;
                    inlineKeyboardMakerForCurrency(message);
                }
                else if (text.equals(C_RUBLE)){
                    C_CHOSEN_CURRENCY = C_RUBLE;
                    inlineKeyboardMakerForCurrency(message);
                }
                else if (C_OPERATION != null && C_CHOSEN_CURRENCY != null){
                    try {
                        double amount = Double.parseDouble(text);
                        if (amount > 0){
                            getResult(amount, message);
                        }
                        else {
                            sendMessageMaker("Manfiy bo'lmagan raqam kiriting\n3444",
                                             "Enter a non-negative number\n3444", "Введите неотрицательное число\n3444", message);
                        }
                    } catch (NumberFormatException e) {
                        sendMessageMaker("Pul miqdorini kiriting kiriting\n3444",
                                         "Enter the amount of money\n3444", "Введите сумму денег, введите\n3444", message);
                    }
                }
                else if (C_OPERATION == null && C_CHOSEN_CURRENCY != null){
                    sendMessageMaker("Yuqoridagi amalni tanlang",
                                     "Choose an action above", "Выберите действие выше", message);
                    deleteMessegaMaker(message);
                }
                else {
                    sendMessageMaker("Botda xabarlarga ro'xsat yo'q",
                                     "Bot messages are not allowed", "Сообщения от ботов запрещены", message);
                    deleteMessegaMaker(message);
                }
            }
        }
        else if (update.hasCallbackQuery()){
            Message message = update.getCallbackQuery().getMessage();
            String data = update.getCallbackQuery().getData();

            if (data.equals("uz")){
                C_DOLLAR = "AQSH dollari";
                C_EURO = "EVRO";
                C_RUBLE = "Rossiya rubli";
                C_YUAN = "Xitoy yuani";
                C_LANG = data;
                C_SUM = "so'm";
                editMessageTextMaker("Siz o'zbek tilini tanladingiz",
                                     "You have selected English", "Вы выбрали русский", message);
                keyboardMakerForCurrency(message);
            }
            else if (data.equals("en")){
                C_DOLLAR = "US Dollar";
                C_EURO = "Euro";
                C_RUBLE = "Russian Ruble";
                C_YUAN = "Yuan Renminbi";
                C_LANG = data;
                C_SUM = "soum";
                editMessageTextMaker("Siz o'zbek tilini tanladingiz",
                        "You have selected English", "Вы выбрали русский", message);
                keyboardMakerForCurrency(message);
            }
            else if (data.equals("ru")) {
                C_DOLLAR = "Доллар США";
                C_EURO = "Евро";
                C_RUBLE = "Российский рубль";
                C_YUAN = "Юань ренминби";
                C_LANG = data;
                C_SUM = "сум";
                editMessageTextMaker("Siz o'zbek tilini tanladingiz",
                        "You have selected English", "Вы выбрали русский", message);
                keyboardMakerForCurrency(message);
            }
            else if (data.equals("toSum")){
                editMessageTextMaker(C_CHOSEN_CURRENCY +"da pul miqdorini kiriting" ,
                                     "Enter the amount in "+C_CHOSEN_CURRENCY ,"Введите сумму денег в "+C_CHOSEN_CURRENCY, message);
                C_OPERATION = "toSum";
            }
            else if (data.equals("fromSum")){
                editMessageTextMaker("So'mda pul miqdorini kiriting","Enter the amount in soums" ,"Введите сумму в сумах", message);
                C_OPERATION = "fromSum";
            }
        }
    }


//    natijani hisoblash
    private void getResult(Double amount, Message message){
        double rate = 0;
        Long resultInt = 0l;
        Long resultFrac = 0l;
        String sendMessageText = "";
        if (C_OPERATION.equals("toSum")){
            if (C_LANG.equals("en")){
                for(Currency currency: currencies) {
                    if (currency.getCcyNm_EN().equals(C_CHOSEN_CURRENCY)){
                        rate = amount * currency.getRate() / currency.getNominal();
                        break;
                    }
                }
            }
            else if (C_LANG.equals("ru")){
                for(Currency currency: currencies) {
                    if (currency.getCcyNm_RU().equals(C_CHOSEN_CURRENCY)){
                        rate = amount * currency.getRate() / currency.getNominal();
                        break;
                    }
                }
            }
            else {
                for (Currency currency : currencies) {
                    if (currency.getCcyNm_UZ().equals(C_CHOSEN_CURRENCY)) {
                        rate = amount * currency.getRate() / currency.getNominal();
                        break;
                    }
                }
            }
            resultInt = (long) (rate);
            resultFrac = (long)(100*(rate - resultInt));
            String result = String.valueOf(resultInt)+"."+String.valueOf(resultFrac);
            C_OPERATION = null;
            sendMessageText = result+" "+C_SUM;
            sendMessageMaker(sendMessageText, sendMessageText, sendMessageText, message);
            C_CHOSEN_CURRENCY = null;
            sendMessageMaker("Pul birligini tanlang", "Select a currency","Выберите валюту", message);

        }
        else if (C_OPERATION.equals("fromSum")){
            if (C_LANG.equals("en")){
                for(Currency currency: currencies) {
                    if (currency.getCcyNm_EN().equals(C_CHOSEN_CURRENCY)){
                        rate = amount * currency.getNominal() / currency.getRate();
                        break;
                    }
                }
            }
            else if (C_LANG.equals("ru")){
                for(Currency currency: currencies) {
                    if (currency.getCcyNm_RU().equals(C_CHOSEN_CURRENCY)){
                        rate = amount * currency.getNominal() / currency.getRate();
                        break;
                    }
                }
            }
            else {
                for (Currency currency : currencies) {
                    if (currency.getCcyNm_UZ().equals(C_CHOSEN_CURRENCY)) {
                        rate = amount * currency.getNominal() / currency.getRate();
                        break;
                    }
                }
            }
            resultInt = (long) (rate);
            resultFrac = (long)(100*(rate - resultInt));
            String result = String.valueOf(resultInt)+"."+String.valueOf(resultFrac);
            C_OPERATION = null;
            sendMessageText = result+" "+C_CHOSEN_CURRENCY;
            sendMessageMaker(sendMessageText, sendMessageText, sendMessageText, message);
            C_CHOSEN_CURRENCY = null;
            keyboardMakerForCurrency(message);
        }
    }

//    set keyboard
    private void keyboardMakerForCurrency(Message message){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        if (C_LANG.equals("en")){
            sendMessage.setText("Choose a currency");
        }
        else if (C_LANG.equals("ru")){
            sendMessage.setText("Выберите валюту");
        }
        else {
            sendMessage.setText("Pul birligini tanlang");
        }

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        List<KeyboardRow> rows = new ArrayList<>();
        replyKeyboardMarkup.setKeyboard(rows);

        KeyboardRow keyboardRow1 = new KeyboardRow();
        rows.add(keyboardRow1);
        keyboardRow1.add(new KeyboardButton(C_DOLLAR));
        keyboardRow1.add(new KeyboardButton(C_RUBLE));

        KeyboardRow keyboardRow2 = new KeyboardRow();
        rows.add(keyboardRow2);
        keyboardRow2.add(C_YUAN);
        keyboardRow2.add(C_EURO);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

//    delete keyboard markap
    private void deleteKeyboardMakerForCurrency(Message message){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));

        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        sendMessage.disableNotification();
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMessageMaker(String textUZ, String textEN, String textRU, Message message){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        if (C_LANG.equals("en")){
            sendMessage.setText(textEN);
        }
        else if (C_LANG.equals("ru")){
            sendMessage.setText(textRU);
        }
        else {
            sendMessage.setText(textUZ);
        }
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void editMessageTextMaker(String textUZ, String textEN, String textRU, Message message){
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(String.valueOf(message.getChatId()));
        editMessageText.setMessageId(message.getMessageId());
        if (C_LANG.equals("en")){
            editMessageText.setText(textEN);
        }
        else if (C_LANG.equals("ru")){
            editMessageText.setText(textRU);
        }
        else {
            editMessageText.setText(textUZ);
        }
        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void deleteMessegaMaker(Message message){
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setMessageId(message.getMessageId());
        deleteMessage.setChatId(String.valueOf(message.getChatId()));
        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void inlineKeyboardMakerForCurrency(Message message){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        if (C_LANG.equals("en")){
            sendMessage.setText("Select an action");
        }
        else if (C_LANG.equals("ru")){
            sendMessage.setText("Выберите действие");
        }
        else {
            sendMessage.setText("Amalni tanlang");
        }

        List<List<InlineKeyboardButton>> collectionRow = new ArrayList<>();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(collectionRow);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        List<InlineKeyboardButton> row = new ArrayList<>();
        collectionRow.add(row);

        InlineKeyboardButton toSum = new InlineKeyboardButton();

        toSum.setText(C_CHOSEN_CURRENCY+"->"+C_SUM);
        toSum.setCallbackData("toSum");
        row.add(toSum);

        InlineKeyboardButton fromSum = new InlineKeyboardButton();
        fromSum.setText(C_SUM+"->"+C_CHOSEN_CURRENCY);
        fromSum.setCallbackData("fromSum");
        row.add(fromSum);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        deleteKeyboardMakerForCurrency(message);
    }

//    set language
    private void setLanguage(String text, Message message){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        sendMessage.setText(text);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        List<List<InlineKeyboardButton>> collectionRow = new ArrayList<>();
        inlineKeyboardMarkup.setKeyboard(collectionRow);

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        collectionRow.add(row1);

        InlineKeyboardButton buttonUZ = new InlineKeyboardButton();
        buttonUZ.setCallbackData("uz");
        row1.add(buttonUZ);

        InlineKeyboardButton buttonEN = new InlineKeyboardButton();
        buttonEN.setCallbackData("en");
        row1.add(buttonEN);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        collectionRow.add(row2);

        InlineKeyboardButton buttonRU = new InlineKeyboardButton();
        buttonRU.setCallbackData("ru");
        row2.add(buttonRU);

        if (C_LANG.equals("en")){
            buttonUZ.setText("Uzbek");
            buttonEN.setText("English");
            buttonRU.setText("Russian");
        }
        else if (C_LANG.equals("ru")){
            buttonUZ.setText("узбекский");
            buttonEN.setText("Инглиз");
            buttonRU.setText("Русский");
        }
        else {
            buttonUZ.setText("O'zbek");
            buttonEN.setText("Ingliz");
            buttonRU.setText("Rus");
        }

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
