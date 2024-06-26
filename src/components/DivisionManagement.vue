<template>
    <div class="background-image"></div>
    <div class="navigation-bar">
        <!-- Butonul de Înapoi sau navigarea -->
        <router-link to="/" class="back-button">Înapoi la Dashboard</router-link>
    </div>
    <div class="division-management-container">
        <h1 class="page-title">Gestionare Ligi & Divizii</h1>
        <div class="league-management">
            <button @click="goToCreateLeaguePage">Crează Ligă Nouă</button>
            <select v-model="selectedLeagueId" @change="fetchDivisions">
                <option disabled value="">Selectează o Ligă</option>
                <option v-for="league in leagues" :key="league.id" :value="league.id">{{ league.name }}</option>
            </select>
        </div>
        <div class="division-display">
            <h2>Diviziile Ligii Selectate</h2>
            <button @click="goToCreateDivisionPage">Crează Divizie Nouă</button> <!-- Buton nou -->
            <!-- Butonul nou pentru distribuirea jucătorilor -->
            <button @click="distributePlayers">Distribuie Jucătorii</button>
            <ul>
                <li v-for="division in divisions" :key="division.id" @click="fetchStandings(division.id)">
                    {{ division.name }}
                </li>
            </ul>
        </div>
        <div class="standings-container" v-if="standings.length > 0">
            <h2 class="division-standings-title">Clasament {{ currentDivisionName }}</h2>
            <div class="standings-header">
                <span class="header-position">#</span>
                <span class="header-name">Nume</span>
                <span class="header-elo">ELO Rating</span>
                <span class="header-wins">Victorii</span>
                <span class="header-losses">Înfrângeri</span>
            </div>
            <div class="standings">
                <div class="standing" v-for="(user, index) in standings" :key="user.id">
                    <span class="position">{{ index + 1 }}</span>
                    <span class="name">{{ user.name }}</span>
                    <span class="elo">{{ user.eloRating }}</span>
                    <span class="wins">{{ user.wins }}</span>
                    <span class="losses">{{ user.losses }}</span>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import axios from 'axios';

export default {
    name: "DivisionManagement",
    data() {
        return {
            leagues: [], // Lista de ligi din baza de date
            divisions: [], // Lista de divizii pentru liga selectată
            selectedLeagueId: '', // ID-ul ligii selectate
            standings: [], // Aici vor fi stocați userii pentru clasament
            currentDivisionName: '' // Numele diviziei curente pentru titlul clasamentului
        };
    },
    methods: {
        createLeague() {
            // Logică pentru crearea unei noi ligi
        },
        goToCreateLeaguePage() {
            this.$router.push({ name: 'LeagueCreate' });
        },
        goToCreateDivisionPage() {
            this.$router.push({ name: 'CreateDivision' }); // Presupunând că ai o rută cu acest nume
        },
        distributePlayers() {
            axios.post(`http://localhost:8088/api/league/distribute/${this.selectedLeagueId}`)
                .then(response => {
                    alert(response.data); // "Players have been successfully distributed in divisions."
                })
                .catch(error => {
                    console.error('Failed to distribute players:', error);
                    alert('Failed to distribute players. Please try again.');
                });
        },
        fetchDivisions() {
            if (!this.selectedLeagueId) {
                console.error('No league selected');
                return;
            }
            axios.get(`http://localhost:8088/api/league/get/divisions/${this.selectedLeagueId}`)
                .then(response => {
                    this.divisions = response.data;
                    console.log('Divisions fetched:', this.divisions);
                })
                .catch(error => console.error('Error fetching divisions:', error));
        },
        fetchUserRecord(userName) {
            // Mai întâi obținem ID-ul utilizatorului pe baza numelui
            axios.get(`http://localhost:8088/api/users/get/id/${userName}`)
                .then(response => {
                    const userId = response.data;
                    // Acum, cu ID-ul, putem obține victoriile și înfrângerile
                    axios.get(`http://localhost:8088/api/users/get/wins/${userId}`)
                        .then(response => {
                            const userIndex = this.standings.findIndex(u => u.name === userName);
                            if (userIndex !== -1) {
                                this.standings[userIndex].wins = response.data;
                            }
                        })
                        .catch(error => {
                            console.error('Error fetching user wins:', error);
                        });
                    axios.get(`http://localhost:8088/api/users/get/losses/${userId}`)
                        .then(response => {
                            const userIndex = this.standings.findIndex(u => u.name === userName);
                            if (userIndex !== -1) {
                                this.standings[userIndex].losses = response.data;
                                // Actualizăm array-ul pentru a declanșa reactivitatea în Vue
                                this.standings = [...this.standings];
                            }
                        })
                        .catch(error => {
                            console.error('Error fetching user losses:', error);
                        });
                })
                .catch(error => {
                    console.error('Error fetching user ID:', error);
                });
        },
        fetchLeagues() {
            // Logică pentru preluarea listei de ligi din baza de date
            const url = 'http://localhost:8088/api/league/get/all';
            axios.get(url)
                .then(response => {
                    this.leagues = response.data;
                })
                .catch(error => {
                    console.error('Error fetching leagues:', error);
                });
        },
        fetchStandings(divisionId) {
            const selectedDivision = this.divisions.find(division => division.id === divisionId);
            if (selectedDivision) {
                this.currentDivisionName = selectedDivision.name;
            }
            const url = `http://localhost:8088/api/division/get/standings/${divisionId}`;
            axios.get(url)
                .then(response => {
                    this.standings = response.data; // Presupunem că avem deja datele de bază ale utilizatorilor
                    this.standings.forEach(user => {
                        this.fetchUserRecord(user.name); // Apelăm noua metodă folosind numele utilizatorului
                    });
                })
                .catch(error => {
                    console.error('Error fetching standings:', error);
                });
        },

    },
    created() {
        this.fetchLeagues(); // Preia ligile când componenta este creată
    }
};
</script>

<style scoped>
.background-image {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    background-image: url('../assets/background_main.jpg');
    background-size: cover;
    background-attachment: fixed;
    z-index: -1;
}

.division-management-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: flex-start;
    min-height: 100vh;
    padding-top: 60px;
    padding-bottom: 20px;
    color: #f0f0f0;
    z-index: 1;
}

.page-title {
    color: #fff;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
    margin-bottom: 20px;
}

.league-management {
    background: rgba(255, 255, 255, 0.8);
    padding: 15px;
    margin-bottom: 20px;
    border-radius: 10px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
    backdrop-filter: blur(5px);
}

.page-title {
    background-color: rgba(255, 255, 255, 0.8);
    color: #2c3e50;
    border: #ccc;
    border-radius: 5px;
    padding: 15px;
}


.league-management button {
    margin-right: 10px;
    padding: 10px 15px;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-weight: bold;
    background-color: #2c3e50;
    color: white;
}

.league-management button:hover {
    background-color: #3e4e61;
}

.league-management select {
    padding: 10px;
    border-radius: 5px;
    border: 1px solid #ccc;
}

.division-display {
    background: rgba(255, 255, 255, 0.8);
    padding: 15px;
    border-radius: 10px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
    backdrop-filter: blur(5px);
    width: 80%;
    max-width: 600px;
}

.division-display h2 {
    color: #2c3e50;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
    margin-bottom: 10px;
}

.division-display ul {
    list-style-type: none;
    padding: 0;
}

.division-display li {
    background: #2c3e50;
    margin-bottom: 5px;
    padding: 10px;
    border-radius: 5px;
    color: white;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
}

.standings-container {
    max-height: 400px;
    overflow-y: auto;
    background: rgba(255, 255, 255, 0.95);
    padding: 20px;
    margin: 20px 0;
    border-radius: 10px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    width: 100%;
    max-width: 800px;
}

.standings {
    width: 100%;
}

.standing {
    display: flex;
    justify-content: space-between;
    padding: 10px;
    border-bottom: 1px solid #ccc;
    background: #f9f9f9;
    margin-bottom: 2px;
}

.standing:hover {
    background: #e8e8e8;
}


.position {
    min-width: 50px;
    text-align: center;
    font-weight: bold;
    color: #2c3e50;
}


.name {
    flex: 1;
    padding: 0 15px;
    color: #2c3e50;
}


.elo {
    min-width: 100px;
    text-align: right;
    font-weight: bold;
    color: #2c3e50;
}

.division-standings-title {
    color: #2c3e50;
    font-size: 1.5em;
    text-align: center;
    margin-bottom: 15px;
    padding: 10px 0;
    width: 100%;
    margin: 0;
}

.standings-header {
    display: flex;
    justify-content: space-between;
    padding: 10px 5px;
    font-weight: bold;
    background: rgba(0, 0, 0, 0.1);
    margin-bottom: 5px;
}

.header-position {
    width: 50px;
    text-align: center;
    color: #2c3e50;
}

.header-name {
    flex: 1;
    text-align: center;
    padding-left: 10px;
    color: #2c3e50;
}

.header-elo {
    width: 100px;
    text-align: right;
    padding-right: 10px;
    color: #2c3e50;
}

.header-wins {
    color: #2c3e50;
}

.header-losses {
    color: #2c3e50;
}

.wins {
    color: #2c3e50;
}

.losses {
    color: #2c3e50;
}

.standings-header,
.standing {
    display: flex;
    justify-content: space-between;
}

.header-position,
.position {
    width: 50px;
}

.header-name,
.name {
    flex: 1;
}

.header-elo,
.elo,
.header-wins,
.wins,
.header-losses,
.losses {
    width: 100px;
    color: #2c3e50;
}

.division-display button {
    padding: 10px 15px;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-weight: bold;
    background-color: #2c3e50;
    color: white;
    margin-top: 10px;
}

.division-display button:hover {
    background-color: #3e4e61;
}

.navigation-bar {
    position: absolute;
    top: 20px;
    left: 50%;
    transform: translateX(-50%);
    z-index: 10;
}

.back-button {
    display: block;
    padding: 10px 20px;
    background: rgba(255, 255, 255, 0.8);
    color: #2c3e50;
    text-align: center;
    border-radius: 5px;
    text-decoration: none;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
    transition: background-color 0.3s ease;
}

.back-button:hover {
    background-color: rgba(255, 255, 255, 0.9);
}

.button {
    padding: 10px 15px;
    margin-top: 10px;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-weight: bold;
    background-color: #2c3e50;
    color: white;
    margin-right: 10px;
}

.button:hover {
    background-color: #3e4e61;
}
</style>