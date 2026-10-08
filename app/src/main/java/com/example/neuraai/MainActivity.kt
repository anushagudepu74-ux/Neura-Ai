package com.example.neuraai

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.neuraai.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val messages = mutableListOf<ChatMessage>()

    private lateinit var adapter: MessageAdapter

    private var lastIntent = "UNKNOWN"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setupChat()

        setupSendButton()

        showWelcomeMessage()
    }

    private fun setupChat() {

        adapter = MessageAdapter(messages)

        binding.recyclerViewMessages.layoutManager =
            LinearLayoutManager(this)

        binding.recyclerViewMessages.adapter = adapter
    }

    private fun setupSendButton() {

        binding.btnSend.setOnClickListener {

            sendMessage()
        }

        binding.etMessage.setOnEditorActionListener { _, _, _ ->

            sendMessage()

            true
        }
    }

    private fun showWelcomeMessage() {

        addBotMessage(
            """
            Hello! 👋

            I am NeuraAI, your conversational assistant.

            You can ask me things like:
            • Hello
            • What time is it?
            • What is today's date?
            • Calculate 25 + 75
            • What can you do?
            • Thank you
            """.trimIndent()
        )
    }

    private fun sendMessage() {

        val userInput =
            binding.etMessage.text.toString().trim()

        if (userInput.isEmpty()) {
            return
        }

        addUserMessage(userInput)

        binding.etMessage.text.clear()

        val response = generateResponse(userInput)

        addBotMessage(response)
    }

    private fun addUserMessage(message: String) {

        messages.add(
            ChatMessage(
                message = message,
                isUser = true
            )
        )

        adapter.notifyItemInserted(messages.size - 1)

        scrollToBottom()
    }

    private fun addBotMessage(message: String) {

        messages.add(
            ChatMessage(
                message = message,
                isUser = false
            )
        )

        adapter.notifyItemInserted(messages.size - 1)

        scrollToBottom()
    }

    private fun scrollToBottom() {

        if (messages.isNotEmpty()) {

            binding.recyclerViewMessages.scrollToPosition(
                messages.size - 1
            )
        }
    }

    private fun generateResponse(input: String): String {

        val text = input.lowercase(Locale.getDefault()).trim()

        return when {

            isGreeting(text) -> {

                lastIntent = "HELLO"

                "Hello! 👋 How can I help you today?"
            }

            containsAny(
                text,
                "time",
                "current time",
                "what time"
            ) -> {

                lastIntent = "TIME"

                val time = SimpleDateFormat(
                    "hh:mm a",
                    Locale.getDefault()
                ).format(Date())

                "The current time is $time."
            }

            containsAny(
                text,
                "date",
                "today",
                "what day"
            ) -> {

                lastIntent = "DATE"

                val date = SimpleDateFormat(
                    "EEEE, dd MMMM yyyy",
                    Locale.getDefault()
                ).format(Date())

                "Today is $date."
            }

            containsAny(
                text,
                "help",
                "what can you do",
                "features"
            ) -> {

                lastIntent = "HELP"

                """
                I can currently help with:

                • Greetings
                • Current time
                • Today's date
                • Basic calculations
                • General questions
                • Fallback responses

                Try asking:
                "Calculate 20 + 30"
                """.trimIndent()
            }

            containsAny(
                text,
                "thank you",
                "thanks",
                "thank"
            ) -> {

                lastIntent = "THANK_YOU"

                "You're welcome! 😊"
            }

            containsAny(
                text,
                "bye",
                "goodbye",
                "see you"
            ) -> {

                lastIntent = "BYE"

                "Goodbye! 👋 Have a great day."
            }

            isCalculation(text) -> {

                lastIntent = "CALCULATOR"

                calculate(text)
            }

            containsAny(
                text,
                "weather",
                "temperature",
                "forecast"
            ) -> {

                lastIntent = "WEATHER"

                """
                I don't have live weather access in this local version.

                You can connect a weather API later to provide real-time weather information.
                """.trimIndent()
            }

            text.contains("what did i ask") -> {

                "Your previous detected intent was: $lastIntent."
            }

            else -> {

                lastIntent = "UNKNOWN"

                fallbackResponse(text)
            }
        }
    }

    private fun isGreeting(text: String): Boolean {

        return containsAny(
            text,
            "hello",
            "hi",
            "hey",
            "good morning",
            "good afternoon",
            "good evening"
        )
    }

    private fun containsAny(
        text: String,
        vararg words: String
    ): Boolean {

        return words.any {
            text.contains(it)
        }
    }

    private fun isCalculation(text: String): Boolean {

        return text.matches(
            Regex(
                ".*\\d+\\s*[+\\-*/]\\s*\\d+.*"
            )
        )
    }

    private fun calculate(text: String): String {

        val regex = Regex(
            "(\\d+(?:\\.\\d+)?)\\s*([+\\-*/])\\s*(\\d+(?:\\.\\d+)?)"
        )

        val match = regex.find(text)

        if (match == null) {

            return "I couldn't understand the calculation."
        }

        val number1 = match.groupValues[1].toDouble()
        val operator = match.groupValues[2]
        val number2 = match.groupValues[3].toDouble()

        val result = when (operator) {

            "+" -> number1 + number2

            "-" -> number1 - number2

            "*" -> number1 * number2

            "/" -> {

                if (number2 == 0.0) {

                    return "I cannot divide by zero."
                }

                number1 / number2
            }

            else -> return "Unsupported calculation."
        }

        return "The answer is ${formatNumber(result)}."
    }

    private fun formatNumber(number: Double): String {

        return if (number % 1.0 == 0.0) {

            number.toLong().toString()

        } else {

            number.toString()
        }
    }

    private fun fallbackResponse(text: String): String {

        return when {

            text.contains("who are you") -> {

                "I am NeuraAI, a conversational assistant designed to detect user intent and provide structured responses."
            }

            text.contains("your name") -> {

                "My name is NeuraAI."
            }

            text.contains("how are you") -> {

                "I'm ready to help! Ask me a question."
            }

            text.contains("computer") -> {

                "I can answer questions and perform several local assistant tasks."
            }

            else -> {

                """
                I didn't fully understand that.

                Try asking me about:
                • Time
                • Date
                • Calculator
                • Help
                • Weather
                • General assistant features
                """.trimIndent()
            }
        }
    }
}