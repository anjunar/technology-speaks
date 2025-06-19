require("dotenv").config();
const axios = require("axios");

const {
    CF_API_TOKEN,
    CF_ZONE_ID,
    CF_DOMAIN
} = process.env;

// Auth-Header
const headers = {
    Authorization: `Bearer ${CF_API_TOKEN}`,
    "Content-Type": "application/json"
};

async function getPublicIp() {
    const res = await axios.get("https://api.ipify.org?format=json");
    return res.data.ip;
}

async function getDnsRecord() {
    const headers = {
        Authorization: `Bearer ${CF_API_TOKEN}`
    };

    const url = `https://api.cloudflare.com/client/v4/zones/${CF_ZONE_ID}/dns_records`;

    const params = {
        type: "A",
        name: CF_DOMAIN
    };

    try {
        const res = await axios.get(url, { headers, params });

        if (res.data.success && res.data.result.length > 0) {
            return res.data.result[0]; // erstes passendes DNS Record-Objekt
        } else {
            throw new Error("DNS-Record nicht gefunden.");
        }
    } catch (error) {
        // Fehler mit genauer Cloudflare-Antwort, falls vorhanden
        if (error.response) {
            throw new Error(
                `Cloudflare API Fehler: ${error.response.status} - ${JSON.stringify(error.response.data)}`
            );
        }
        throw error;
    }
}

async function updateDnsRecord(recordId, ip) {
    const url = `https://api.cloudflare.com/client/v4/zones/${CF_ZONE_ID}/dns_records/${recordId}`;

    const body = {
        type: "A",
        name: CF_DOMAIN,
        content: ip,
        ttl: 1,
        proxied: true
    };

    const res = await axios.put(url, body, { headers });

    return res.data;
}

(async () => {
    try {
        const ip = await getPublicIp();
        console.log("🌐 Öffentliche IP:", ip);

        const record = await getDnsRecord();
        console.log("📄 Aktueller DNS-Record:", record.content);

        if (record.content !== ip) {
            console.log("🔄 IP hat sich geändert. Aktualisiere...");
            const result = await updateDnsRecord(record.id, ip);
            if (result.success) {
                console.log("✅ DNS-Eintrag aktualisiert.");
            } else {
                console.error("❌ Fehler bei Update:", result.errors);
            }
        } else {
            console.log("✅ IP ist aktuell. Kein Update nötig.");
        }
    } catch (err) {
        console.error("❌ Fehler:", err.message);
    }
})();
