import React, { useState, useEffect, useMemo } from 'react';
import { Row, Col, Card, Button, Container } from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';

const hexToRgba = (hex, alpha = 1) => {
  if (!hex) {
    return `rgba(255, 255, 255, ${alpha})`;
  }

  let normalized = hex.replace('#', '');
  if (normalized.length === 3) {
    normalized = normalized.split('').map((char) => `${char}${char}`).join('');
  }

  const numeric = parseInt(normalized, 16);
  if (Number.isNaN(numeric)) {
    return `rgba(255, 255, 255, ${alpha})`;
  }

  const r = (numeric >> 16) & 255;
  const g = (numeric >> 8) & 255;
  const b = numeric & 255;

  return `rgba(${r}, ${g}, ${b}, ${alpha})`;
};

function Home() {
  const [currentSlide, setCurrentSlide] = useState(0);
  const [isVisible, setIsVisible] = useState(false);

  const heroSlides = useMemo(() => ([
    {
      background: 'https://images.unsplash.com/photo-1489599856772-16c0924af999?ixlib=rb-4.0.3&auto=format&fit=crop&w=2070&q=80',
      title: 'Experience Cinema Magic',
      subtitle: 'Book premium seats for the latest blockbusters',
      accent: '#ff6b6b'
    },
    {
      background: 'https://images.unsplash.com/photo-1596727147705-61a532a659bd?ixlib=rb-4.0.3&auto=format&fit=crop&w=2070&q=80',
      title: 'Luxury Movie Experience',
      subtitle: 'State-of-the-art theatres with Dolby Atmos',
      accent: '#4ecdc4'
    },
    {
      background: 'https://images.unsplash.com/photo-1536440136628-849c177e76a1?ixlib=rb-4.0.3&auto=format&fit=crop&w=2070&q=80',
      title: 'Your Story Begins Here',
      subtitle: 'Discover amazing movies and unforgettable moments',
      accent: '#45b7d1'
    }
  ]), []);

  useEffect(() => {
    heroSlides.forEach((slide) => {
      const image = new Image();
      image.src = slide.background;
    });
  }, [heroSlides]);

  useEffect(() => {
    setIsVisible(true);
    const interval = setInterval(() => {
      setCurrentSlide((prev) => (prev + 1) % heroSlides.length);
    }, 5000);
    return () => clearInterval(interval);
  }, [heroSlides.length]);

  const currentHero = heroSlides[currentSlide];
  const overlayGradient = `linear-gradient(135deg, rgba(15, 23, 42, 0.72), ${hexToRgba(currentHero.accent, 0.5)})`;

  return (
    <div style={{ marginTop: '-76px' }}>
      {/* Revolutionary Hero Section */}
      <div className="revolutionary-hero" style={{
        height: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        position: 'relative',
        overflow: 'hidden'
      }}>
        <div className="hero-backdrop">
          <img
            key={currentHero.background}
            src={currentHero.background}
            alt={currentHero.title}
            className="hero-backdrop-image"
            loading="lazy"
            decoding="async"
          />
        </div>
        <div className="hero-overlay" style={{ background: overlayGradient }} />

        {/* Main Hero Content */}
        <Container className="text-center position-relative">
          <div className={`hero-content ${isVisible ? 'animate-in' : ''}`} style={{
            animation: isVisible ? 'fadeInUp 1s ease-out' : '',
            color: 'white',
            position: 'relative',
            zIndex: 2
          }}>
            {/* Movie Reel Animation */}
            <div className="movie-reel-container mb-4">
              <div className="movie-reel" style={{
                width: '120px',
                height: '120px',
                margin: '0 auto 2rem',
                background: `linear-gradient(45deg, ${currentHero.accent}, #ffd700)`,
                borderRadius: '50%',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontSize: '4rem',
                animation: 'spin 10s linear infinite, pulse 2s ease-in-out infinite alternate',
                boxShadow: `0 0 40px ${currentHero.accent}40`
              }}>
                🎬
              </div>
            </div>

            {/* Dynamic Title */}
            <h1 className="hero-title" style={{
              fontSize: 'clamp(3rem, 8vw, 6rem)',
              fontWeight: '900',
              color: 'white',
              textShadow: '2px 2px 4px rgba(0,0,0,0.7)',
              marginBottom: '1.5rem',
              background: 'none',
              filter: 'none'
            }}>
              {currentHero.title}
            </h1>

            <p className="hero-subtitle" style={{
              fontSize: 'clamp(1.2rem, 4vw, 2rem)',
              fontWeight: '300',
              marginBottom: '3rem',
              textShadow: '1px 1px 2px rgba(0,0,0,0.5)',
              maxWidth: '800px',
              margin: '0 auto 3rem'
            }}>
              {currentHero.subtitle}
            </p>

            {/* Animated Action Buttons */}
            <div className="hero-actions" style={{
              display: 'flex',
              gap: '2rem',
              justifyContent: 'center',
              flexWrap: 'wrap',
              marginBottom: '4rem'
            }}>
              <LinkContainer to="/movies">
                <Button
                  size="lg"
                  className="hero-btn primary-btn"
                  style={{
                    background: `linear-gradient(135deg, ${currentHero.accent}, #667eea)`,
                    border: 'none',
                    borderRadius: '50px',
                    padding: '1rem 3rem',
                    fontSize: '1.2rem',
                    fontWeight: '600',
                    textTransform: 'uppercase',
                    letterSpacing: '1px',
                    position: 'relative',
                    overflow: 'hidden',
                    transition: 'all 0.3s ease',
                    animation: 'pulse 2s infinite'
                  }}
                >
                  <span style={{ position: 'relative', zIndex: 2 }}>
                    🎭 Explore Movies
                  </span>
                </Button>
              </LinkContainer>

              <LinkContainer to="/theatres">
                <Button
                  variant="outline-light"
                  size="lg"
                  className="hero-btn secondary-btn"
                  style={{
                    borderRadius: '50px',
                    padding: '1rem 3rem',
                    fontSize: '1.2rem',
                    fontWeight: '600',
                    textTransform: 'uppercase',
                    letterSpacing: '1px',
                    borderWidth: '2px',
                    transition: 'all 0.3s ease',
                    background: 'rgba(255, 255, 255, 0.1)'
                  }}
                >
                  🏢 Find Theatres
                </Button>
              </LinkContainer>
            </div>

          </div>
        </Container>

        {/* Slide Thumbnails */}
        <div className="hero-thumbnails" style={{
          position: 'absolute',
          bottom: '2.5rem',
          left: '50%',
          transform: 'translateX(-50%)',
          display: 'flex',
          gap: '1rem',
          zIndex: 3
        }}>
          {heroSlides.map((slide, index) => (
            <button
              key={slide.title}
              type="button"
              onClick={() => setCurrentSlide(index)}
              aria-label={`Show ${slide.title}`}
              className={`hero-thumbnail ${index === currentSlide ? 'is-active' : ''}`}
              style={{
                borderColor: index === currentSlide ? currentHero.accent : 'rgba(255, 255, 255, 0.4)',
                boxShadow: index === currentSlide
                  ? `0 0 0 3px rgba(255, 255, 255, 0.25), 0 18px 28px ${hexToRgba(currentHero.accent, 0.35)}`
                  : '0 12px 22px rgba(15, 23, 42, 0.35)'
              }}
            >
              <img
                src={slide.background}
                alt={slide.title}
                className="hero-thumbnail-image"
                loading="lazy"
                decoding="async"
                onError={(e) => {
                  e.target.style.display = 'none';
                  e.target.parentElement.style.background = `linear-gradient(135deg, ${slide.accent}, #333)`;
                }}
                onLoad={(e) => {
                  e.target.style.display = 'block';
                }}
              />
              <span
                className="hero-thumbnail-overlay"
                style={{ background: `linear-gradient(135deg, rgba(15, 23, 42, 0.45), ${hexToRgba(slide.accent, 0.55)})` }}
              />
            </button>
          ))}
        </div>
      </div>

      {/* Enhanced Statistics Section */}
      <div className="stats-section" style={{
        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
        color: 'white',
        padding: '4rem 0'
      }}>
        <Container>
          <Row className="text-center">
            <Col md={3} className="mb-4">
              <div className="stat-item">
                <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>🎬</div>
                <h3 style={{ fontSize: '2.5rem', fontWeight: 'bold' }}>500+</h3>
                <p>Movies Available</p>
              </div>
            </Col>
            <Col md={3} className="mb-4">
              <div className="stat-item">
                <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>🏢</div>
                <h3 style={{ fontSize: '2.5rem', fontWeight: 'bold' }}>150+</h3>
                <p>Partner Theatres</p>
              </div>
            </Col>
            <Col md={3} className="mb-4">
              <div className="stat-item">
                <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>👥</div>
                <h3 style={{ fontSize: '2.5rem', fontWeight: 'bold' }}>1M+</h3>
                <p>Happy Customers</p>
              </div>
            </Col>
            <Col md={3} className="mb-4">
              <div className="stat-item">
                <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>🌟</div>
                <h3 style={{ fontSize: '2.5rem', fontWeight: 'bold' }}>4.8</h3>
                <p>Average Rating</p>
              </div>
            </Col>
          </Row>
        </Container>
      </div>

      <Container className="py-5">
        {/* Featured Movies Section */}
        <div className="text-center mb-5">
          <h2 className="display-4 mb-3">🎭 Featured Movies</h2>
          <p className="lead">Discover the latest blockbusters and timeless classics</p>
        </div>

        <Row className="mb-5">
          <Col md={4} className="mb-4">
            <Card className="movie-card h-100">
              <Card.Img
                variant="top"
                src="https://images.unsplash.com/photo-1536440136628-849c177e76a1?ixlib=rb-4.0.3&auto=format&fit=crop&w=1000&q=80"
                className="movie-poster"
                alt="Action Movie"
              />
              <Card.Body>
                <div className="movie-genre">Action</div>
                <Card.Title className="movie-title">Epic Adventures</Card.Title>
                <div className="rating-stars">★★★★☆</div>
                <Card.Text>
                  Experience heart-pounding action and breathtaking stunts in this epic adventure.
                </Card.Text>
                <LinkContainer to="/movies">
                  <Button variant="primary">Watch Now</Button>
                </LinkContainer>
              </Card.Body>
            </Card>
          </Col>
          <Col md={4} className="mb-4">
            <Card className="movie-card h-100">
              <Card.Img
                variant="top"
                src="https://images.unsplash.com/photo-1485846234645-a62644f84728?ixlib=rb-4.0.3&auto=format&fit=crop&w=1000&q=80"
                className="movie-poster"
                alt="Drama Movie"
              />
              <Card.Body>
                <div className="movie-genre">Drama</div>
                <Card.Title className="movie-title">Emotional Journey</Card.Title>
                <div className="rating-stars">★★★★★</div>
                <Card.Text>
                  A powerful story that will touch your heart and leave you thinking long after.
                </Card.Text>
                <LinkContainer to="/movies">
                  <Button variant="primary">Watch Now</Button>
                </LinkContainer>
              </Card.Body>
            </Card>
          </Col>
          <Col md={4} className="mb-4">
            <Card className="movie-card h-100">
              <Card.Img
                variant="top"
                src="https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?ixlib=rb-4.0.3&auto=format&fit=crop&w=1000&q=80"
                className="movie-poster"
                alt="Comedy Movie"
              />
              <Card.Body>
                <div className="movie-genre">Comedy</div>
                <Card.Title className="movie-title">Laugh Out Loud</Card.Title>
                <div className="rating-stars">★★★★☆</div>
                <Card.Text>
                  Get ready for non-stop laughter with this hilarious comedy masterpiece.
                </Card.Text>
                <LinkContainer to="/movies">
                  <Button variant="primary">Watch Now</Button>
                </LinkContainer>
              </Card.Body>
            </Card>
          </Col>
        </Row>

        {/* Features Section */}
        <div className="text-center mb-5">
          <h2 className="display-5 mb-3">Why Choose MyMovie?</h2>
        </div>

        <Row className="mb-5">
          <Col md={4} className="mb-4">
            <Card className="feature-card">
              <Card.Body>
                <div className="feature-icon">🎬</div>
                <h4>Latest Movies</h4>
                <Card.Text>
                  Access to the newest releases from Hollywood, Bollywood, and regional cinema.
                  Never miss a blockbuster again!
                </Card.Text>
                <LinkContainer to="/movies">
                  <Button variant="primary">Browse Movies</Button>
                </LinkContainer>
              </Card.Body>
            </Card>
          </Col>
          <Col md={4} className="mb-4">
            <Card className="feature-card">
              <Card.Body>
                <div className="feature-icon">🏢</div>
                <h4>Premium Theatres</h4>
                <Card.Text>
                  Experience movies in state-of-the-art theatres with Dolby Atmos sound and
                  luxury recliner seats.
                </Card.Text>
                <LinkContainer to="/theatres">
                  <Button variant="primary">Find Theatres</Button>
                </LinkContainer>
              </Card.Body>
            </Card>
          </Col>
          <Col md={4} className="mb-4">
            <Card className="feature-card">
              <Card.Body>
                <div className="feature-icon">🎫</div>
                <h4>Easy Booking</h4>
                <Card.Text>
                  Book your tickets in seconds with our intuitive interface. Select seats,
                  choose timing, and pay securely.
                </Card.Text>
                <LinkContainer to="/shows">
                  <Button variant="primary">Book Tickets</Button>
                </LinkContainer>
              </Card.Body>
            </Card>
          </Col>
        </Row>

        {/* Call to Action Section */}
        <div className="cta-section">
          <h2 className="display-5 mb-3">🌟 Join the MyMovie Family</h2>
          <p className="lead mb-4">
            Over 1 million movie lovers trust MyMovie for their entertainment needs.
            Join them today and never miss a show!
          </p>
          <LinkContainer to="/register">
            <Button variant="light" size="lg" className="me-3">
              🚀 Sign Up Free
            </Button>
          </LinkContainer>
          <LinkContainer to="/movies">
            <Button variant="outline-light" size="lg">
              🎭 Explore Movies
            </Button>
          </LinkContainer>
        </div>
      </Container>
    </div>
  );
}

export default Home;
