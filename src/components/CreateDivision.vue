<template>
    <div class="background-image"></div>
    <div class="navigation-bar">
        <!-- Butonul de Înapoi sau navigarea -->
        <router-link to="/" class="back-button">Înapoi la Dashboard</router-link>
    </div>
    <div class="create-division-container">
        <h1>Crează o nouă Divizie</h1>
        <form @submit.prevent="submitForm">
            <div class="form-group">
                <label for="divisionName">Nume Divizie</label>
                <input type="text" id="divisionName" v-model="division.name" required>
            </div>
            <div class="form-group">
                <label for="leagueSelect">Alege Liga</label>
                <select id="leagueSelect" v-model="division.league_id" required>
                    <option v-for="league in leagues" :key="league.id" :value="league.id">{{ league.name }}</option>
                </select>
            </div>
            <div class="form-group">
                <label for="divisionRank">Poziția Diviziei</label>
                <select id="divisionRank" v-model="division.rank" required>
                    <option v-for="rankInfo in availableRanks" :key="rankInfo.rank" :value="rankInfo.rank">
                        {{ rankInfo.rank }} - {{ rankInfo.divisionName }}
                    </option>
                </select>
            </div>
            <button type="submit">Crează Divizie</button>
        </form>
    </div>
</template>
  
<script>
import axios from 'axios';

export default {
    name: 'CreateDivision',
    data() {
        return {
            leagues: [], // Lista de ligi disponibile
            division: {
                name: '',
                league_id: null,
                rank: null
            },
            availableRanks: []
        };
    },
    methods: {
        fetchLeagues() {
            axios.get('http://localhost:8088/api/league/get/all')
                .then(response => {
                    this.leagues = response.data;
                })
                .catch(error => {
                    console.error('Error fetching leagues:', error);
                });
        },
        calculateAvailableRanks() {
            if (this.division.league_id) {
                axios.get(`http://localhost:8088/api/league/get/divisions/${this.division.league_id}`)
                    .then(response => {
                        const divisions = response.data;
                        this.availableRanks = divisions.map((division, index) => ({
                            rank: index + 1,
                            divisionName: division.name || 'Poziție liberă'
                        }));
                        // Adaug un rang suplimentar pentru noua divizie
                        this.availableRanks.push({ rank: divisions.length + 1, divisionName: 'Poziție liberă' });
                    })
                    .catch(error => {
                        console.error('Error fetching divisions:', error);
                    });
            }
        },

        submitForm() {
            const divisionData = {
                name: this.division.name,
                rank: this.division.rank
            };
            axios.post(`http://localhost:8088/api/division/create/${this.division.league_id}`, divisionData)
                .then(() => {
                    alert('Divizia a fost creată cu succes!');
                    this.division = { name: '', league_id: null, rank: null }; 
                    this.calculateAvailableRanks(); 
                    this.fetchLeagues();
                })
                .catch(error => {
                    console.error('A apărut o eroare la crearea diviziei:', error);
                    alert('A apărut o eroare la crearea diviziei. Verificați datele și încercați din nou.');
                });
        }
    },
    watch: {
        'division.league_id': function () {
            this.calculateAvailableRanks();
        }
    },
    mounted() {
        this.fetchLeagues();
        this.calculateAvailableRanks();
    }
};
</script>
  
<style scoped>


.create-division-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    min-height: 50vh;
    padding: 10px;
    color: #2c3e50;
    background: rgba(255, 255, 255, 0.8);
    border-radius: 15px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
    backdrop-filter: blur(5px);
    width: 80%;
    max-width: 600px;
    margin: auto;
    margin-top: 4%;
}

.form-group {
    margin-bottom: 20px;
}

.form-group label {
    display: block;
    margin-bottom: 5px;
    font-weight: 600;
    color: #333;
}

.form-group input,
.form-group select {
    width: 100%;
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 4px;
    box-sizing: border-box;
}

button {
    width: 100%;
    padding: 10px 0;
    background-color: #2c3e50;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 1em;
    font-weight: 600;
    transition: background-color 0.3s ease;
}

button:hover {
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
</style>