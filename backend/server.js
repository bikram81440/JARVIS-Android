const express = require('express');
const cors = require('cors');
require('dotenv').config();

const app = express();
app.use(express.json());
app.use(cors());

const PORT = process.env.PORT || 3000;

app.get('/health', (req, res) => {
    res.json({ status: 'ok' });
});

app.post('/api/chat', async (req, res) => {
    try {
        const apiKey = process.env.OPENAI_API_KEY;
        if (!apiKey) {
            return res.status(500).json({
                success: false,
                reply: "I am terribly sorry, Sir, but the server OPENAI_API_KEY is not configured."
            });
        }

        const { message, systemInstruction } = req.body;
        if (!message) {
            return res.status(400).json({
                success: false,
                reply: "I am afraid no message was provided, Sir."
            });
        }

        const systemPrompt = systemInstruction || "You are J.A.R.V.I.S., a sophisticated, intelligent and witty personal AI assistant. Use a refined professional British-inspired tone. Address the user as Sir when appropriate. Keep responses concise, natural, helpful and polite.";

        const response = await fetch("https://api.openai.com/v1/chat/completions", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${apiKey}`
            },
            body: JSON.stringify({
                model: process.env.OPENAI_MODEL || "gpt-4o-mini",
                messages: [
                    { role: "system", content: systemPrompt },
                    { role: "user", content: message }
                ],
                temperature: 0.7
            })
        });

        if (!response.ok) {
            const errorText = await response.text();
            console.error("OpenAI API Error:", errorText);
            return res.status(502).json({
                success: false,
                reply: "I am terribly sorry, Sir, but the neural network (OpenAI API) encountered an error."
            });
        }

        const data = await response.json();
        const reply = data.choices?.[0]?.message?.content;

        if (!reply) {
            return res.status(502).json({
                success: false,
                reply: "I am afraid I received an empty response from the neural network, Sir."
            });
        }

        res.json({
            success: true,
            reply: reply
        });

    } catch (error) {
        console.error("Chat Server Error:", error);
        res.status(500).json({
            success: false,
            reply: "I am terribly sorry, Sir, but a server error has occurred."
        });
    }
});

app.listen(PORT, () => {
    console.log(`JARVIS backend running on port ${PORT}`);
});
