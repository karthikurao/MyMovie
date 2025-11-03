import React, { useState, useEffect } from 'react';
import { Row, Col, Card, Button, Alert, Table, Tab, Tabs, Container, Badge, Modal, Form } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { ensureSeconds } from '../utils/datetime';

function AdminDashboard() {
  const [movies, setMovies] = useState([]);
  const [theatres, setTheatres] = useState([]);
  const [shows, setShows] = useState([]);
  const [bookings, setBookings] = useState([]);
  const [screens, setScreens] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [activeTab, setActiveTab] = useState('overview');
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [deleteItem, setDeleteItem] = useState(null);
  const [showMovieModal, setShowMovieModal] = useState(false);
  const [editingMovie, setEditingMovie] = useState(null);
  const [movieForm, setMovieForm] = useState({
    movieName: '',
    movieGenre: '',
    movieHours: '',
    language: '',
    description: '',
    imageUrl: ''
  });
  const [showTheatreModal, setShowTheatreModal] = useState(false);
  const [editingTheatre, setEditingTheatre] = useState(null);
  const [theatreForm, setTheatreForm] = useState({
    theatreName: '',
    theatreCity: '',
    managerName: '',
    managerContact: ''
  });
  const [showShowModal, setShowShowModal] = useState(false);
  const [editingShow, setEditingShow] = useState(null);
  const [showForm, setShowForm] = useState({
    showName: '',
    movieId: '',
    theatreId: '',
    screenId: '',
    showStartTime: '',
    showEndTime: ''
  });
  const [showScreenModal, setShowScreenModal] = useState(false);
  const [editingScreen, setEditingScreen] = useState(null);
  const [screenForm, setScreenForm] = useState({
    theatreId: '',
    screenName: '',
    rows: '10',
    columns: '12'
  });
  const navigate = useNavigate();

  useEffect(() => {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    if (!user.userId || user.role !== 'ADMIN') {
      navigate('/login');
      return;
    }
    fetchAdminData();
  }, [navigate]);

  const fetchAdminData = async () => {
    try {
      setLoading(true);
      setError('');
      const [moviesRes, theatresRes, showsRes, bookingsRes, screensRes] = await Promise.all([
        axios.get('/api/movies', { timeout: 10000 }),
        axios.get('/api/theatres', { timeout: 10000 }),
        axios.get('/api/shows', { timeout: 10000 }),
        axios.get('/api/bookings', { timeout: 10000 }),
        axios.get('/api/screens', { timeout: 10000 })
      ]);

      setMovies(moviesRes.data || []);
      setTheatres(theatresRes.data || []);
      setShows(showsRes.data || []);
      setBookings(bookingsRes.data || []);
      setScreens(screensRes.data || []);
    } catch (err) {
      console.error('Error fetching admin data:', err);
      if (err.code === 'ECONNABORTED') {
        setError('Request timeout. Please check if the backend server is running.');
      } else if (err.request) {
        setError('Unable to connect to server. Please ensure the backend is running.');
      } else {
        setError('Failed to fetch admin data. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = (type, item) => {
    setDeleteItem({ type, item });
    setShowDeleteModal(true);
  };

  const confirmDelete = async () => {
    if (!deleteItem) return;

    try {
      const { type, item } = deleteItem;
      const endpoints = {
        movie: `/api/movies/${item.movieId}`,
        theatre: `/api/theatres/${item.theatreId}`,
        show: `/api/shows/${item.showId}`,
        booking: `/api/bookings/${item.bookingId || item.ticketId}`,
        screen: `/api/screens/${item.screenId}`
      };

      await axios.delete(endpoints[type]);

      // Update state
      switch (type) {
        case 'movie':
          setMovies(movies.filter(m => m.movieId !== item.movieId));
          break;
        case 'theatre':
          setTheatres(theatres.filter(t => t.theatreId !== item.theatreId));
          setScreens(screens.filter(screen => screen.theatreId !== item.theatreId));
          setShows(shows.filter(s => s.theatreId !== item.theatreId));
          break;
        case 'show':
          setShows(shows.filter(s => s.showId !== item.showId));
          break;
        case 'screen':
          setScreens(screens.filter(s => s.screenId !== item.screenId));
          setShows(shows.filter(s => s.screenId !== item.screenId));
          break;
        case 'booking':
          setBookings(bookings.filter(b => (b.bookingId || b.ticketId) !== (item.bookingId || item.ticketId)));
          break;
        default:
          break;
      }

      setShowDeleteModal(false);
      setDeleteItem(null);
    } catch (err) {
      alert(`Failed to delete ${deleteItem.type}.`);
    }
  };

  const handleMovieFormChange = (e) => {
    setMovieForm({
      ...movieForm,
      [e.target.name]: e.target.value
    });
  };

  const handleAddMovie = () => {
    setEditingMovie(null);
    setMovieForm({
      movieName: '',
      movieGenre: '',
      movieHours: '',
      language: '',
      description: '',
      imageUrl: ''
    });
    setShowMovieModal(true);
  };

  const handleEditMovie = (movie) => {
    setEditingMovie(movie);
    setMovieForm({
      movieName: movie.movieName || '',
      movieGenre: movie.movieGenre || '',
      movieHours: movie.movieHours || '',
      language: movie.language || '',
      description: movie.description || '',
      imageUrl: movie.imageUrl || ''
    });
    setShowMovieModal(true);
  };

  const handleMovieSubmit = async (e) => {
    e.preventDefault();
    try {
      let response;
      if (editingMovie) {
        // Update existing movie
        response = await axios.put(`/api/movies/${editingMovie.movieId}`, movieForm);
        setMovies(movies.map(m => m.movieId === editingMovie.movieId ? response.data : m));
      } else {
        // Add new movie
        response = await axios.post('/api/movies', movieForm);
        setMovies(prev => [...prev, response.data]);
      }
      setShowMovieModal(false);
      setEditingMovie(null);
      setMovieForm({
        movieName: '',
        movieGenre: '',
        movieHours: '',
        language: '',
        description: '',
        imageUrl: ''
      });
    } catch (err) {
      console.error('Error saving movie:', err);
      alert('Failed to save movie. Please try again.');
    }
  };

  const handleTheatreFormChange = (e) => {
    setTheatreForm({
      ...theatreForm,
      [e.target.name]: e.target.value
    });
  };

  const handleAddTheatre = () => {
    setEditingTheatre(null);
    setTheatreForm({
      theatreName: '',
      theatreCity: '',
      managerName: '',
      managerContact: ''
    });
    setShowTheatreModal(true);
  };

  const handleEditTheatre = (theatre) => {
    setEditingTheatre(theatre);
    setTheatreForm({
      theatreName: theatre.theatreName || '',
      theatreCity: theatre.theatreCity || '',
      managerName: theatre.managerName || '',
      managerContact: theatre.managerContact || ''
    });
    setShowTheatreModal(true);
  };

  const handleTheatreSubmit = async (e) => {
    e.preventDefault();
    // Basic validation for manager contact (7-15 digits; allow formatting characters)
    const digits = String(theatreForm.managerContact || '').replace(/\D/g, '');
    if (digits.length < 7 || digits.length > 15) {
      alert('Please enter a valid manager contact number (7-15 digits).');
      return;
    }
    try {
      let response;
      if (editingTheatre) {
        response = await axios.put(`/api/theatres/${editingTheatre.theatreId}`, theatreForm);
        setTheatres(theatres.map(t => t.theatreId === editingTheatre.theatreId ? response.data : t));
      } else {
        response = await axios.post('/api/theatres', theatreForm);
        setTheatres(prev => [...prev, response.data]);
      }
      setShowTheatreModal(false);
      setEditingTheatre(null);
      setTheatreForm({
        theatreName: '',
        theatreCity: '',
        managerName: '',
        managerContact: ''
      });
    } catch (err) {
      console.error('Error saving theatre:', err);
      alert('Failed to save theatre. Please try again.');
    }
  };

  const handleShowFormChange = (e) => {
    const { name, value } = e.target;
    if (name === 'theatreId') {
      setShowForm({
        ...showForm,
        theatreId: value,
        screenId: ''
      });
      return;
    }
    setShowForm({
      ...showForm,
      [name]: value
    });
  };

  const handleAddShow = () => {
    setEditingShow(null);
    setShowForm({
      showName: '',
      movieId: '',
      theatreId: '',
      screenId: '',
      showStartTime: '',
      showEndTime: ''
    });
    setShowShowModal(true);
  };

  const toDateTimeLocal = (value) => {
    if (!value) {
      return '';
    }
    return String(value).slice(0, 16);
  };

  const handleEditShow = (show) => {
    setEditingShow(show);
    setShowForm({
      showName: show.showName || '',
      movieId: show.movieId ? String(show.movieId) : '',
      theatreId: show.theatreId ? String(show.theatreId) : '',
      screenId: show.screenId ? String(show.screenId) : '',
      showStartTime: toDateTimeLocal(show.showStartTime),
      showEndTime: toDateTimeLocal(show.showEndTime)
    });
    setShowShowModal(true);
  };

  const handleShowSubmit = async (e) => {
    e.preventDefault();
    const payload = {
      showName: String(showForm.showName || '').trim(),
      movieId: showForm.movieId ? Number(showForm.movieId) : null,
      theatreId: showForm.theatreId ? Number(showForm.theatreId) : null,
      screenId: showForm.screenId ? Number(showForm.screenId) : null,
      showStartTime: ensureSeconds(showForm.showStartTime),
      showEndTime: ensureSeconds(showForm.showEndTime)
    };

    // Required selections
    if (!payload.showName) {
      alert('Please enter a show name.');
      return;
    }
    if (!payload.movieId || !payload.theatreId || !payload.screenId) {
      alert('Please select movie, theatre, and screen.');
      return;
    }

    // Screen must belong to theatre
    const selectedScreen = screens.find(s => s.screenId === payload.screenId);
    if (!selectedScreen || selectedScreen.theatreId !== payload.theatreId) {
      alert('Selected screen does not belong to the chosen theatre.');
      return;
    }

    // Time validation
    const start = payload.showStartTime ? new Date(payload.showStartTime) : null;
    const end = payload.showEndTime ? new Date(payload.showEndTime) : null;
    if (!start || !end) {
      alert('Please provide both start and end time.');
      return;
    }
    if (end <= start) {
      alert('End time must be after start time.');
      return;
    }

    try {
      let response;
      if (editingShow) {
        response = await axios.put(`/api/shows/${editingShow.showId}`, { ...payload, showId: editingShow.showId });
        setShows(shows.map(s => s.showId === editingShow.showId ? response.data : s));
      } else {
        response = await axios.post('/api/shows', payload);
        setShows(prev => [...prev, response.data]);
      }
      setShowShowModal(false);
      setEditingShow(null);
      setShowForm({
        showName: '',
        movieId: '',
        theatreId: '',
        screenId: '',
        showStartTime: '',
        showEndTime: ''
      });
    } catch (err) {
      console.error('Error saving show:', err);
      alert('Failed to save show. Please try again.');
    }
  };

  const handleScreenFormChange = (e) => {
    setScreenForm({
      ...screenForm,
      [e.target.name]: e.target.value
    });
  };

  const handleAddScreen = () => {
    setEditingScreen(null);
    setScreenForm({ theatreId: '', screenName: '', rows: '10', columns: '12' });
    setShowScreenModal(true);
  };

  const handleEditScreen = (screen) => {
    setEditingScreen(screen);
    setScreenForm({
      theatreId: String(screen.theatreId || ''),
      screenName: screen.screenName || '',
      rows: String(screen.rows || ''),
      columns: String(screen.columns || '')
    });
    setShowScreenModal(true);
  };

  const handleScreenSubmit = async (e) => {
    e.preventDefault();
    const payload = {
      theatreId: screenForm.theatreId ? Number(screenForm.theatreId) : null,
      screenName: screenForm.screenName,
      rows: Number(screenForm.rows),
      columns: Number(screenForm.columns)
    };
    if (!payload.theatreId || !payload.screenName || !payload.rows || !payload.columns) {
      alert('Please fill all required screen fields.');
      return;
    }
    try {
      let response;
      if (editingScreen) {
        response = await axios.put(`/api/screens/${editingScreen.screenId}`, { ...payload, screenId: editingScreen.screenId });
        setScreens(screens.map(s => s.screenId === editingScreen.screenId ? response.data : s));
      } else {
        response = await axios.post('/api/screens', payload);
        setScreens(prev => [...prev, response.data]);
      }
      setShowScreenModal(false);
      setEditingScreen(null);
      setScreenForm({ theatreId: '', screenName: '', rows: '10', columns: '12' });
    } catch (err) {
      console.error('Error saving screen:', err);
      alert('Failed to save screen. Please try again.');
    }
  };

  const totalRevenue = bookings.reduce((sum, booking) => sum + (booking.totalCost || 0), 0);

  if (loading) {
    return (
      <Container className="text-center py-5">
        <div className="spinner-border text-primary" style={{ width: '3rem', height: '3rem' }}>
          <span className="visually-hidden">Loading...</span>
        </div>
        <h4 className="mt-3">Loading Admin Dashboard...</h4>
        <p className="text-muted">Fetching system data</p>
      </Container>
    );
  }

  return (
    <Container className="py-5">
      {/* Admin Header */}
      <div className="text-center mb-5">
        <h1 className="display-4 mb-3">👨‍💼 Admin Dashboard</h1>
        <p className="lead">Manage your movie booking system efficiently</p>
      </div>

      {error && (
        <Alert variant="danger" className="mb-4">
          <Alert.Heading>🚫 Error</Alert.Heading>
          <p>{error}</p>
          <hr />
          <Button variant="outline-danger" onClick={fetchAdminData}>
            🔄 Retry
          </Button>
        </Alert>
      )}

      {/* Admin Stats */}
      <Row className="mb-5">
        <Col md={3} className="mb-4">
          <Card className="feature-card h-100 text-center">
            <Card.Body>
              <div className="feature-icon" style={{ fontSize: '3rem', marginBottom: '1rem' }}>🎬</div>
              <h3 className="text-primary mb-2">{movies.length}</h3>
              <p className="mb-0">Total Movies</p>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3} className="mb-4">
          <Card className="feature-card h-100 text-center">
            <Card.Body>
              <div className="feature-icon" style={{ fontSize: '3rem', marginBottom: '1rem' }}>🏢</div>
              <h3 className="text-info mb-2">{theatres.length}</h3>
              <p className="mb-0">Partner Theatres</p>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3} className="mb-4">
          <Card className="feature-card h-100 text-center">
            <Card.Body>
              <div className="feature-icon" style={{ fontSize: '3rem', marginBottom: '1rem' }}>🎫</div>
              <h3 className="text-warning mb-2">{bookings.length}</h3>
              <p className="mb-0">Total Bookings</p>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3} className="mb-4">
          <Card className="feature-card h-100 text-center">
            <Card.Body>
              <div className="feature-icon" style={{ fontSize: '3rem', marginBottom: '1rem' }}>💰</div>
              <h3 className="text-success mb-2">₹{totalRevenue.toLocaleString()}</h3>
              <p className="mb-0">Total Revenue</p>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Admin Tabs */}
      <Card className="shadow-sm" style={{ borderRadius: '15px', border: 'none' }}>
        <Tabs
          activeKey={activeTab}
          onSelect={(k) => setActiveTab(k)}
          className="nav-justified"
          style={{ borderRadius: '15px 15px 0 0' }}
        >
          <Tab eventKey="overview" title="📊 Overview">
            <div className="p-4">
              <Row>
                <Col md={6} className="mb-4">
                  <Card className="h-100" style={{ borderRadius: '10px' }}>
                    <Card.Header className="bg-primary text-white">
                      <h6 className="mb-0">🎬 Recent Movies</h6>
                    </Card.Header>
                    <Card.Body>
                      {movies.slice(0, 5).map(movie => (
                        <div key={movie.movieId} className="d-flex justify-content-between align-items-center py-2 border-bottom">
                          <div>
                            <strong>{movie.movieName}</strong>
                            <br />
                            <small className="text-muted">{movie.movieGenre} • {movie.language}</small>
                          </div>
                          <Badge bg="primary">{movie.movieHours}</Badge>
                        </div>
                      ))}
                    </Card.Body>
                  </Card>
                </Col>
                <Col md={6} className="mb-4">
                  <Card className="h-100" style={{ borderRadius: '10px' }}>
                    <Card.Header className="bg-success text-white">
                      <h6 className="mb-0">🎫 Recent Bookings</h6>
                    </Card.Header>
                    <Card.Body>
                      {bookings.slice(0, 5).map((booking, index) => (
                        <div key={booking.ticketId || booking.bookingId || index} className="d-flex justify-content-between align-items-center py-2 border-bottom">
                          <div>
                            <strong>#{booking.ticketId || booking.bookingId || `BK${index + 1001}`}</strong>
                            <br />
                            <small className="text-muted">{booking.movieName || 'Movie'}</small>
                          </div>
                          <Badge bg="success">₹{(booking.totalCost || 500).toLocaleString()}</Badge>
                        </div>
                      ))}
                    </Card.Body>
                  </Card>
                </Col>
              </Row>
            </div>
          </Tab>

          <Tab eventKey="movies" title="🎬 Movies">
            <div className="p-4">
              <div className="d-flex justify-content-between align-items-center mb-4">
                <h5>Manage Movies</h5>
                <Button variant="primary" style={{ borderRadius: '10px' }} onClick={handleAddMovie}>
                  ➕ Add Movie
                </Button>
              </div>
              <div className="table-responsive">
                <Table hover>
                  <thead className="bg-light">
                    <tr>
                      <th>Movie Name</th>
                      <th>Genre</th>
                      <th>Language</th>
                      <th>Duration</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {movies.map(movie => (
                      <tr key={movie.movieId}>
                        <td>
                          <strong>{movie.movieName}</strong>
                          <br />
                          <small className="text-muted">{movie.description}</small>
                        </td>
                        <td>
                          <Badge className="movie-genre">{movie.movieGenre}</Badge>
                        </td>
                        <td>{movie.language}</td>
                        <td>{movie.movieHours}</td>
                        <td>
                          <div className="d-flex gap-1">
                            <Button
                              variant="outline-primary"
                              size="sm"
                              style={{ borderRadius: '8px' }}
                              onClick={() => handleEditMovie(movie)}
                            >
                              ✏️ Edit
                            </Button>
                            <Button
                              variant="outline-danger"
                              size="sm"
                              style={{ borderRadius: '8px' }}
                              onClick={() => handleDelete('movie', movie)}
                            >
                              🗑️ Delete
                            </Button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </Table>
              </div>
            </div>
          </Tab>

          <Tab eventKey="theatres" title="🏢 Theatres">
            <div className="p-4">
              <div className="d-flex justify-content-between align-items-center mb-4">
                <h5>Manage Theatres</h5>
                <Button variant="primary" style={{ borderRadius: '10px' }} onClick={handleAddTheatre}>
                  ➕ Add Theatre
                </Button>
              </div>
              <div className="table-responsive">
                <Table hover>
                  <thead className="bg-light">
                    <tr>
                      <th>Theatre Name</th>
                      <th>City</th>
                      <th>Manager</th>
                      <th>Contact</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {theatres.map(theatre => (
                      <tr key={theatre.theatreId}>
                        <td><strong>{theatre.theatreName}</strong></td>
                        <td>
                          <Badge bg="info">{theatre.theatreCity}</Badge>
                        </td>
                        <td>{theatre.managerName}</td>
                        <td>{theatre.managerContact}</td>
                        <td>
                          <div className="d-flex gap-1">
                            <Button
                              variant="outline-primary"
                              size="sm"
                              style={{ borderRadius: '8px' }}
                              onClick={() => handleEditTheatre(theatre)}
                            >
                              ✏️ Edit
                            </Button>
                            <Button
                              variant="outline-danger"
                              size="sm"
                              style={{ borderRadius: '8px' }}
                              onClick={() => handleDelete('theatre', theatre)}
                            >
                              🗑️ Delete
                            </Button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </Table>
              </div>
            </div>
          </Tab>

          <Tab eventKey="shows" title="🎭 Shows">
            <div className="p-4">
              <div className="d-flex justify-content-between align-items-center mb-4">
                <h5>Manage Shows</h5>
                <Button
                  variant="primary"
                  style={{ borderRadius: '10px' }}
                  onClick={handleAddShow}
                  disabled={!movies.length || !theatres.length || !screens.length}
                >
                  ➕ Add Show
                </Button>
              </div>
              <div className="table-responsive">
                <Table hover>
                  <thead className="bg-light">
                    <tr>
                      <th>Show Name</th>
                      <th>Movie</th>
                      <th>Theatre</th>
                      <th>Date & Time</th>
                      <th>Screen</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {shows.map(show => {
                      const movie = movies.find(m => m.movieId === show.movieId);
                      const theatre = theatres.find(t => t.theatreId === show.theatreId);
                      const screen = screens.find(sc => sc.screenId === show.screenId);
                      return (
                        <tr key={show.showId}>
                          <td><strong>{show.showName}</strong></td>
                          <td>{movie ? movie.movieName : 'Unknown Movie'}</td>
                          <td>{theatre ? theatre.theatreName : 'Unknown Theatre'}</td>
                          <td>
                            <small>
                              {new Date(show.showStartTime).toLocaleString()} — {new Date(show.showEndTime).toLocaleTimeString()}
                            </small>
                          </td>
                          <td>
                            <Badge bg="secondary">{screen ? `${screen.screenName}` : `Screen #${show.screenId}`}</Badge>
                          </td>
                          <td>
                            <div className="d-flex gap-1">
                              <Button
                                variant="outline-primary"
                                size="sm"
                                style={{ borderRadius: '8px' }}
                                onClick={() => handleEditShow(show)}
                              >
                                ✏️ Edit
                              </Button>
                              <Button
                                variant="outline-danger"
                                size="sm"
                                style={{ borderRadius: '8px' }}
                                onClick={() => handleDelete('show', show)}
                              >
                                🗑️ Delete
                              </Button>
                            </div>
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </Table>
              </div>
            </div>
          </Tab>

          <Tab eventKey="bookings" title="🎫 Bookings">
            <div className="p-4">
              <h5 className="mb-4">Customer Bookings</h5>
              <div className="table-responsive">
                <Table hover>
                  <thead className="bg-light">
                    <tr>
                      <th>Booking ID</th>
                      <th>Show ID</th>
                      <th>Date</th>
                      <th>Status</th>
                      <th>Amount</th>
                      <th>Payment Mode</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {bookings.map((booking, index) => (
                      <tr key={booking.ticketId || booking.bookingId || index}>
                        <td><strong>#{booking.ticketId || booking.bookingId || `BK${index + 1001}`}</strong></td>
                        <td>{booking.showId || 'N/A'}</td>
                        <td>
                          <small>{booking.bookingDate ? new Date(booking.bookingDate).toLocaleDateString() : new Date().toLocaleDateString()}</small>
                        </td>
                        <td>
                          <Badge bg={booking.transactionStatus === 'CONFIRMED' ? 'success' : 'warning'}>
                            {booking.transactionStatus || 'CONFIRMED'}
                          </Badge>
                        </td>
                        <td>
                          <strong className="text-success">
                            ₹{(booking.totalCost || 500).toLocaleString()}
                          </strong>
                        </td>
                        <td>{booking.transactionMode || 'ONLINE'}</td>
                        <td>
                          <div className="d-flex gap-1">
                            <Button variant="outline-info" size="sm" style={{ borderRadius: '8px' }}>
                              👁️ View
                            </Button>
                            <Button
                              variant="outline-danger"
                              size="sm"
                              style={{ borderRadius: '8px' }}
                              onClick={() => handleDelete('booking', booking)}
                            >
                              ❌ Cancel
                            </Button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </Table>
              </div>
            </div>
          </Tab>
        </Tabs>
      </Card>

      {/* Movie Form Modal */}
      <Modal show={showMovieModal} onHide={() => setShowMovieModal(false)} size="lg" centered>
        <Modal.Header closeButton style={{ background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', color: 'white' }}>
          <Modal.Title>{editingMovie ? '✏️ Edit Movie' : '➕ Add Movie'}</Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleMovieSubmit}>
          <Modal.Body className="p-4">
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Movie Name *</Form.Label>
                  <Form.Control
                    type="text"
                    name="movieName"
                    value={movieForm.movieName}
                    onChange={handleMovieFormChange}
                    required
                    placeholder="Enter movie name"
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Genre *</Form.Label>
                  <Form.Select
                    name="movieGenre"
                    value={movieForm.movieGenre}
                    onChange={handleMovieFormChange}
                    required
                  >
                    <option value="">Select Genre</option>
                    <option value="Action">Action</option>
                    <option value="Adventure">Adventure</option>
                    <option value="Animation">Animation</option>
                    <option value="Comedy">Comedy</option>
                    <option value="Crime">Crime</option>
                    <option value="Drama">Drama</option>
                    <option value="Fantasy">Fantasy</option>
                    <option value="Horror">Horror</option>
                    <option value="Romance">Romance</option>
                    <option value="Sci-Fi">Sci-Fi</option>
                    <option value="Thriller">Thriller</option>
                    <option value="War">War</option>
                  </Form.Select>
                </Form.Group>
              </Col>
            </Row>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Duration *</Form.Label>
                  <Form.Control
                    type="text"
                    name="movieHours"
                    value={movieForm.movieHours}
                    onChange={handleMovieFormChange}
                    required
                    placeholder="e.g., 2h 30m"
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Language *</Form.Label>
                  <Form.Select
                    name="language"
                    value={movieForm.language}
                    onChange={handleMovieFormChange}
                    required
                  >
                    <option value="">Select Language</option>
                    <option value="English">English</option>
                    <option value="Hindi">Hindi</option>
                    <option value="Tamil">Tamil</option>
                    <option value="Telugu">Telugu</option>
                    <option value="Malayalam">Malayalam</option>
                    <option value="Kannada">Kannada</option>
                    <option value="Bengali">Bengali</option>
                    <option value="Marathi">Marathi</option>
                    <option value="Punjabi">Punjabi</option>
                    <option value="Korean">Korean</option>
                    <option value="Japanese">Japanese</option>
                    <option value="French">French</option>
                    <option value="Spanish">Spanish</option>
                  </Form.Select>
                </Form.Group>
              </Col>
            </Row>
            <Form.Group className="mb-3">
              <Form.Label>Description</Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                name="description"
                value={movieForm.description}
                onChange={handleMovieFormChange}
                placeholder="Enter movie description"
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Image URL</Form.Label>
              <Form.Control
                type="text"
                name="imageUrl"
                value={movieForm.imageUrl}
                onChange={handleMovieFormChange}
                placeholder="Enter image URL"
              />
            </Form.Group>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setShowMovieModal(false)}>
              Cancel
            </Button>
            <Button variant="primary" type="submit">
              {editingMovie ? 'Update Movie' : 'Add Movie'}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>

      {/* Theatre Form Modal */}
      <Modal show={showTheatreModal} onHide={() => setShowTheatreModal(false)} centered>
        <Modal.Header closeButton style={{ background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', color: 'white' }}>
          <Modal.Title>{editingTheatre ? '✏️ Edit Theatre' : '➕ Add Theatre'}</Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleTheatreSubmit}>
          <Modal.Body className="p-4">
            <Form.Group className="mb-3">
              <Form.Label>Theatre Name *</Form.Label>
              <Form.Control
                type="text"
                name="theatreName"
                value={theatreForm.theatreName}
                onChange={handleTheatreFormChange}
                required
                placeholder="Enter theatre name"
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>City *</Form.Label>
              <Form.Control
                type="text"
                name="theatreCity"
                value={theatreForm.theatreCity}
                onChange={handleTheatreFormChange}
                required
                placeholder="Enter city"
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Manager Name *</Form.Label>
              <Form.Control
                type="text"
                name="managerName"
                value={theatreForm.managerName}
                onChange={handleTheatreFormChange}
                required
                placeholder="Enter manager name"
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Manager Contact *</Form.Label>
              <Form.Control
                type="text"
                name="managerContact"
                value={theatreForm.managerContact}
                onChange={handleTheatreFormChange}
                required
                placeholder="Enter contact number"
              />
            </Form.Group>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setShowTheatreModal(false)}>
              Cancel
            </Button>
            <Button variant="primary" type="submit">
              {editingTheatre ? 'Update Theatre' : 'Add Theatre'}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>

      {/* Show Form Modal */}
      <Modal show={showShowModal} onHide={() => setShowShowModal(false)} size="lg" centered>
        <Modal.Header closeButton style={{ background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', color: 'white' }}>
          <Modal.Title>{editingShow ? '✏️ Edit Show' : '➕ Add Show'}</Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleShowSubmit}>
          <Modal.Body className="p-4">
            <Row>
              <Col md={6} className="mb-3">
                <Form.Group>
                  <Form.Label>Show Name *</Form.Label>
                  <Form.Control
                    type="text"
                    name="showName"
                    value={showForm.showName}
                    onChange={handleShowFormChange}
                    required
                    placeholder="Enter show name"
                  />
                </Form.Group>
              </Col>
              <Col md={6} className="mb-3">
                <Form.Group>
                  <Form.Label>Movie *</Form.Label>
                  <Form.Select
                    name="movieId"
                    value={showForm.movieId}
                    onChange={handleShowFormChange}
                    required
                  >
                    <option value="">Select movie</option>
                    {movies.map(movie => (
                      <option key={movie.movieId} value={movie.movieId}>
                        {movie.movieName}
                      </option>
                    ))}
                  </Form.Select>
                </Form.Group>
              </Col>
            </Row>
            <Row>
              <Col md={6} className="mb-3">
                <Form.Group>
                  <Form.Label>Theatre *</Form.Label>
                  <Form.Select
                    name="theatreId"
                    value={showForm.theatreId}
                    onChange={handleShowFormChange}
                    required
                  >
                    <option value="">Select theatre</option>
                    {theatres.map(theatre => (
                      <option key={theatre.theatreId} value={theatre.theatreId}>
                        {theatre.theatreName} ({theatre.theatreCity})
                      </option>
                    ))}
                  </Form.Select>
                </Form.Group>
              </Col>
              <Col md={6} className="mb-3">
                <Form.Group>
                  <Form.Label>Screen *</Form.Label>
                  <Form.Select
                    name="screenId"
                    value={showForm.screenId}
                    onChange={handleShowFormChange}
                    required
                  >
                    <option value="">Select screen</option>
                    {screens
                      .filter(screen => !showForm.theatreId || String(screen.theatreId) === showForm.theatreId)
                      .map(screen => (
                        <option key={screen.screenId} value={screen.screenId}>
                          {screen.screenName} • Theatre #{screen.theatreId}
                        </option>
                      ))}
                  </Form.Select>
                  {!screens.length && (
                    <Form.Text muted>
                      No screens available. Add screens via backend before creating shows.
                    </Form.Text>
                  )}
                </Form.Group>
              </Col>
            </Row>
            <Row>
              <Col md={6} className="mb-3">
                <Form.Group>
                  <Form.Label>Start Time *</Form.Label>
                  <Form.Control
                    type="datetime-local"
                    name="showStartTime"
                    value={showForm.showStartTime}
                    onChange={handleShowFormChange}
                    required
                  />
                </Form.Group>
              </Col>
              <Col md={6} className="mb-3">
                <Form.Group>
                  <Form.Label>End Time *</Form.Label>
                  <Form.Control
                    type="datetime-local"
                    name="showEndTime"
                    value={showForm.showEndTime}
                    onChange={handleShowFormChange}
                    required
                  />
                </Form.Group>
              </Col>
            </Row>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setShowShowModal(false)}>
              Cancel
            </Button>
            <Button variant="primary" type="submit" disabled={!screens.length}>
              {editingShow ? 'Update Show' : 'Add Show'}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>

      {/* Delete Confirmation Modal */}
      <Modal show={showDeleteModal} onHide={() => setShowDeleteModal(false)} centered>
        <Modal.Header closeButton style={{ background: 'linear-gradient(135deg, #dc3545 0%, #c82333 100%)', color: 'white' }}>
          <Modal.Title>⚠️ Confirm Deletion</Modal.Title>
        </Modal.Header>
        <Modal.Body className="text-center p-4">
          <div className="mb-3">
            <div style={{ fontSize: '3rem' }}>🗑️</div>
          </div>
          <h5>Are you sure you want to delete this {deleteItem?.type}?</h5>
          <p className="text-muted">
            This action cannot be undone.
          </p>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="outline-secondary" onClick={() => setShowDeleteModal(false)}>
            Cancel
          </Button>
          <Button variant="danger" onClick={confirmDelete}>
            Delete
          </Button>
        </Modal.Footer>
      </Modal>
    </Container>
  );
}

export default AdminDashboard;
