#!/bin/bash

API_URL="http://3.34.99.164/api/translation"
USER_ACCOUNT_NO=1

declare -A TEXTS
TEXTS["Korean"]="안녕하세요 개발자 지망생입니다. 열심히 노력해서 회사에 기여하겠습니다."
TEXTS["English"]="Hello, I am an aspiring developer. I will work hard to contribute to the company."
TEXTS["Chinese"]="你好，我是一名有抱负的开发者。我会努力为公司做出贡献。"
TEXTS["Spanish"]="Hola, soy un aspirante a desarrollador. Trabajaré duro para contribuir a la empresa."
TEXTS["Portuguese"]="Olá, sou um aspirante a desenvolvedor. Vou trabalhar duro para contribuir com a empresa."
TEXTS["German"]="Hallo, ich bin ein angehender Entwickler. Ich werde hart arbeiten, um zum Unternehmen beizutragen."
TEXTS["Czech"]="Ahoj, jsem nadějný vývojář. Budu tvrdě pracovat, abych přispěl k firmě."
TEXTS["Slovak"]="Ahoj, som začínajúci vývojár. Budem tvrdo pracovať, aby som prispel k spoločnosti."
TEXTS["Russian"]="Здравствуйте, я начинающий разработчик. Я буду усердно работать, чтобы внести вклад в компанию."
TEXTS["Hindi"]="नमस्कार, मैं एक उम्मीदवार डेवलपर हूं। मैं कंपनी में योगदान देने के लिए कड़ी मेहनत करूंगा।"
TEXTS["Indonesian"]="Halo, saya adalah calon pengembang. Saya akan bekerja keras untuk berkontribusi pada perusahaan."
TEXTS["Arabic"]="مرحبًا ، أنا مطور طموح. سأعمل بجد للمساهمة في الشركة."
TEXTS["Vietnamese"]="Xin chào, tôi là một lập trình viên đang trên đà phát triển. Tôi sẽ cố gắng làm việc để đóng góp cho công ty."

LANGUAGES=(
    "Korean"
    "English"
    "Chinese"
    "Spanish"
    "Portuguese"
    "German"
    "Czech"
    "Slovak"
    "Russian"
    "Hindi"
    "Indonesian"
    "Arabic"
    "Vietnamese"
)

for from_language in "${LANGUAGES[@]}"; do
    for to_language in "${LANGUAGES[@]}"; do
        if [ "$from_language" != "$to_language" ]; then
            echo "Translating from $from_language to $to_language:"
            curl -X POST "$API_URL" -H "Content-Type: application/json" -d "{
                \"userAccountNo\": $USER_ACCOUNT_NO,
                \"requestText\": \"${TEXTS[$from_language]}\",
                \"resultLanguageName\": \"$to_language\"
            }"
            echo ""
        fi
    done
done

