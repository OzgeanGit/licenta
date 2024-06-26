<template>
    <div class="background-image"></div>
    <div class="dashboard">
      <h1 class="title">Panoul Ligii de Șah</h1>
      <p class="description">Informații de bază despre liga de șah, inclusiv o scurtă descriere.</p>
      <nav class="navigation">
        <ul class="nav-list">
          <li class="nav-item"><router-link to="/create-user" class="nav-link">Creare Jucători</router-link></li>
          <li class="nav-item"><router-link to="/divisions" class="nav-link">Gestionare Ligă și Divizii</router-link></li>
          <li class="nav-item"><router-link to="/matchmaking" class="nav-link">Matchmaking</router-link></li>
          <li class="nav-item"><router-link to="/results" class="nav-link">Rezultate și Clasamente</router-link></li>
        </ul>
      </nav>
    </div>
  </template>
  
  <style scoped>
  .dashboard {
    max-width: 800px;
    margin: 50px auto;
    padding: 20px;
    text-align: center;
    background: rgba(255, 255, 255, 0.8); 
    border-radius: 10px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
    backdrop-filter: blur(5px);
  }
  
  .title {
    font-size: 2.5rem;
    color: #2c3e50; 
    margin-bottom: 10px;
    font-family: 'Roboto Slab', serif; 
  }
  
  .description {
    font-size: 1.1rem;
    margin-bottom: 30px;
    color: #6c757d; 
    font-family: 'Open Sans', sans-serif;
  }
  
  .navigation .nav-list {
    list-style-type: none;
    padding: 0;
    margin: 0;
  }
  
  .navigation .nav-item {
    margin: 10px 0;
  }
  
  .navigation .nav-link {
    text-decoration: none;
    font-size: 1.2rem;
    color: #007bff; 
    transition: color 0.3s ease;
  }
  
  .navigation .nav-link:hover {
    color: #0056b3; 
    text-decoration: underline;
  }
  .background-image {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw; 
  height: 100vh; 
  background-image: url("../assets/background_main.jpg");
  background-size: cover;
  background-attachment: fixed;
  z-index: -1; 
  overflow: hidden;
}
  </style>
  