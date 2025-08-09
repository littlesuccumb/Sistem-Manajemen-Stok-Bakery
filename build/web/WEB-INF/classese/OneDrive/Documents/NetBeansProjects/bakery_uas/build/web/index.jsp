<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">

  <!-- Bootstrap CSS & Icons -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet"
        integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.5.0/font/bootstrap-icons.css">

  <!-- Custom CSS -->
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/home.css">

  <title>D'Bakery | Home</title>
</head>
<body>
  <!-- Navbar/Header -->
  <nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow fixed-top">
    <div class="container-fluid">
      <a class="navbar-brand" href="#">
        <i class="bi bi-cup-hot-fill me-1"></i> D'Bakery
      </a>
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
              aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
          <li class="nav-item"><a class="nav-link active" href="#home">Home</a></li>
          <li class="nav-item"><a class="nav-link" href="#menu">Menu</a></li>
          <li class="nav-item"><a class="nav-link" href="#about">About</a></li>
          <li class="nav-item"><a class="nav-link" href="#contact">Contact</a></li>
        </ul>
        <div class="d-flex">
          <a href="${pageContext.request.contextPath}/auth.jsp?action=login" class="btn btn-outline-light me-2">
            <i class="bi bi-box-arrow-in-right"></i> Login
          </a>
          <a href="${pageContext.request.contextPath}/auth.jsp?action=register" class="btn btn-light">
            <i class="bi bi-person-plus"></i> Register
          </a>
        </div>
      </div>
    </div>
  </nav>

  <!-- Hero Section -->
  <header class="jumbotron text-center bg-light" id="home" style="padding-top: 120px;">
    <h1 class="display-4 fw-bold">Great Taste in Every Bite</h1>
    <p class="lead">Premium bakery goods made with love and tradition.</p>
    <a href="#menu" class="btn btn-warning btn-lg mt-3">Explore Menu</a>
  </header>

  <!-- Menu Section -->
  <section id="menu" class="py-5 bg-white">
    <div class="container">
      <div class="text-center mb-5">
        <h2 class="fw-bold">Our Menu</h2>
        <p>Temukan berbagai pilihan roti, kue, dan pastry terbaik kami.</p>
      </div>
      <div class="row">
        <div class="col-md-4 mb-4">
          <div class="card h-100 shadow border-0 hover-shadow rounded-4 overflow-hidden">
            <img src="https://images.pexels.com/photos/960662/pexels-photo-960662.jpeg?auto=compress&cs=tinysrgb&w=600" class="card-img-top" alt="Roti" style="height: 220px; object-fit: cover;">
            <div class="card-body text-center">
              <h5 class="card-title fw-bold">Roti Gandum</h5>
              <p class="card-text text-muted">Roti sehat berbahan gandum utuh, cocok untuk sarapan.</p>
            </div>
          </div>
        </div>
        <div class="col-md-4 mb-4">
          <div class="card h-100 shadow border-0 hover-shadow rounded-4 overflow-hidden">
            <img src="https://images.pexels.com/photos/2144200/pexels-photo-2144200.jpeg?auto=compress&cs=tinysrgb&w=600" class="card-img-top" alt="Kue" style="height: 220px; object-fit: cover;">
            <div class="card-body text-center">
              <h5 class="card-title fw-bold">Chocolate Cake</h5>
              <p class="card-text text-muted">Kue cokelat lembut dengan lapisan ganache spesial.</p>
            </div>
          </div>
        </div>
        <div class="col-md-4 mb-4">
          <div class="card h-100 shadow border-0 hover-shadow rounded-4 overflow-hidden">
            <img src="https://images.pexels.com/photos/30176147/pexels-photo-30176147.jpeg?auto=compress&cs=tinysrgb&w=600" class="card-img-top" alt="Pastry" style="height: 220px; object-fit: cover;">
            <div class="card-body text-center">
              <h5 class="card-title fw-bold">Croissant</h5>
              <p class="card-text text-muted">Pastry renyah khas Perancis, disajikan hangat.</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>

  <!-- Wave + About -->
  <div style="position: relative;">
    <svg viewBox="0 0 1440 100" style="display:block" xmlns="http://www.w3.org/2000/svg"><path fill="#f5cf8e" d="M0,64L48,80C96,96,192,128,288,128C384,128,480,96,576,69.3C672,43,768,21,864,16C960,11,1056,21,1152,26.7C1248,32,1344,32,1392,32L1440,32L1440,0L1392,0C1344,0,1248,0,1152,0C1056,0,960,0,864,0C768,0,672,0,576,0C480,0,384,0,288,0C192,0,96,0,48,0L0,0Z"></path></svg>

    <section id="about" class="py-5" style="background-color: #f5cf8e;">
      <div class="container">
        <div class="row align-items-center">
          <div class="col-md-6 mb-4">
            <h2 class="fw-bold">Tentang Kami</h2>
            <p class="mb-4">
              D'Bakery berdiri sejak 2020 dan berkomitmen untuk menyajikan produk bakery terbaik dengan bahan alami dan rasa istimewa yang tak terlupakan.<br><br>
              Kami percaya bahwa setiap gigitan harus membawa kenangan hangat dan bahagia. Temukan kenikmatan dari bakery premium hanya di D'Bakery.
            </p>
            <a href="#contact" class="btn btn-primary">Hubungi Kami</a>
          </div>
          <div class="col-md-6 text-center">
            <img src="https://images.pexels.com/photos/2696064/pexels-photo-2696064.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940" alt="Bakery" class="img-fluid rounded-4 shadow">
          </div>
        </div>
      </div>
    </section>

    <svg viewBox="0 0 1440 100" style="display:block" xmlns="http://www.w3.org/2000/svg"><path fill="#f5cf8e" d="M0,64L48,80C96,96,192,128,288,128C384,128,480,96,576,69.3C672,43,768,21,864,16C960,11,1056,21,1152,26.7C1248,32,1344,32,1392,32L1440,32L1440,0L1392,0C1344,0,1248,0,1152,0C1056,0,960,0,864,0C768,0,672,0,576,0C480,0,384,0,288,0C192,0,96,0,48,0L0,0Z"></path></svg>
  </div>

  <!-- Contact Section -->
  <section id="contact" class="py-5 bg-white">
    <div class="container">
      <div class="text-center mb-4">
        <h2 class="fw-bold">Kontak Kami</h2>
        <p>Hubungi kami untuk pemesanan atau pertanyaan lebih lanjut.</p>
      </div>
      <div class="row justify-content-center">
        <div class="col-md-6">
          <form>
            <div class="mb-3">
              <input type="text" class="form-control" placeholder="Nama Anda" required>
            </div>
            <div class="mb-3">
              <input type="email" class="form-control" placeholder="Email" required>
            </div>
            <div class="mb-3">
              <textarea class="form-control" rows="4" placeholder="Pesan Anda" required></textarea>
            </div>
            <button type="submit" class="btn btn-warning w-100">Kirim Pesan</button>
          </form>
        </div>
      </div>
    </div>
  </section>


  <!-- Bootstrap Bundle JS -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.0/dist/js/bootstrap.bundle.min.js"
          integrity="sha384-U1DAWAznBHeqEIlVSCgzq+c9gqGAJn5c/t99JyeKa9xxaYpSvHU5awsuZVVFIhvj"
          crossorigin="anonymous"></script>
</body>
</html>
