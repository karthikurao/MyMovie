import React from 'react';
import { Container, Row, Col, Card, Badge } from 'react-bootstrap';

const experiences = [
    {
        icon: '🎧',
        title: 'Immersive Audio Zones',
        description:
            'Lose yourself in multidimensional Dolby Atmos soundscapes engineered for goosebumps in every seat.',
        vibe: 'sonic',
        accent: {
            background: 'linear-gradient(135deg, rgba(102, 126, 234, 0.18), rgba(31, 64, 104, 0.75))',
            borderColor: 'rgba(102, 126, 234, 0.35)',
        },
        highlights: ['Dolby Atmos certified', 'Adaptive volume control', 'Personalized audio presets'],
    },
    {
        icon: '🛋️',
        title: 'Tailored Comfort Suites',
        description:
            'Reclining pods with climate control, wireless charging, and concierge ordering for gourmet bites.',
        vibe: 'comfort',
        accent: {
            background: 'linear-gradient(135deg, rgba(241, 39, 17, 0.12), rgba(245, 175, 25, 0.45))',
            borderColor: 'rgba(245, 175, 25, 0.35)',
        },
        highlights: ['Heated recliners', 'Chef-inspired menu', 'Ambient lighting presets'],
    },
    {
        icon: '🌌',
        title: 'Cinematic Multiverses',
        description:
            'Step inside themed lobbies, selfie portals, and AR trails designed to extend the story beyond the screen.',
        vibe: 'story',
        accent: {
            background: 'linear-gradient(135deg, rgba(69, 183, 209, 0.14), rgba(69, 104, 220, 0.45))',
            borderColor: 'rgba(69, 183, 209, 0.35)',
        },
        highlights: ['Interactive AR quests', 'Collectible badges', 'Themed lounge pop-ups'],
    },
    {
        icon: '⚡',
        title: 'Instant Express Booking',
        description:
            'One-tap checkout with biometric unlock, predictive seat suggestions, and group-sync planning.',
        vibe: 'speed',
        accent: {
            background: 'linear-gradient(135deg, rgba(240, 147, 251, 0.16), rgba(245, 87, 108, 0.45))',
            borderColor: 'rgba(240, 147, 251, 0.35)',
        },
        highlights: ['Face & fingerprint login', 'Seat sentiment engine', 'Invite link coordination'],
    },
    {
        icon: '🎟️',
        title: 'Collectors Club Access',
        description:
            'Unlock premiere drops, filmmaker AMAs, and holographic keepsakes with our membership tiers.',
        vibe: 'loyalty',
        accent: {
            background: 'linear-gradient(135deg, rgba(255, 215, 0, 0.14), rgba(255, 159, 67, 0.45))',
            borderColor: 'rgba(255, 215, 0, 0.35)',
        },
        highlights: ['Priority screenings', 'Creator lounges', 'Digital collectibles'],
    },
    {
        icon: '🌱',
        title: 'Eco-Lux Theatres',
        description:
            'Solar-backed venues, zero-plastic concessions, and carbon-offset tickets for kinder cinema nights.',
        vibe: 'eco',
        accent: {
            background: 'linear-gradient(135deg, rgba(52, 211, 153, 0.12), rgba(16, 185, 129, 0.45))',
            borderColor: 'rgba(16, 185, 129, 0.35)',
        },
        highlights: ['Carbon-neutral tickets', 'Reusable dineware', 'Living wall lounges'],
    },
];

const vibeLabels = {
    sonic: 'Sound',
    comfort: 'Comfort',
    story: 'Storyworld',
    speed: 'Speed',
    loyalty: 'Loyalty',
    eco: 'Sustainability',
};

function CinematicExperiences() {
    return (
        <section className="cinema-experiences py-5" aria-labelledby="experiences-heading">
            <Container>
                <div className="section-heading text-center mb-5">
                    <Badge bg="light" text="dark" pill className="section-badge">
                        Elevate Every Screening
                    </Badge>
                    <h2 id="experiences-heading" className="display-5 fw-bold mb-3">
                        Crafted Experiences Beyond the Seat
                    </h2>
                    <p className="lead mx-auto" style={{ maxWidth: '720px' }}>
                        CinemaHub is reimagining the movie night—from ambient lighting that syncs with the
                        climax, to concierge-crafted menus inspired by your film. Dive into a product vision
                        shaped with storytellers and superfans alike.
                    </p>
                </div>

                <Row className="g-4">
                    {experiences.map((experience) => (
                        <Col key={experience.title} lg={4} md={6}>
                            <Card
                                className="experience-card h-100"
                                style={{
                                    background: experience.accent.background,
                                    borderColor: experience.accent.borderColor,
                                }}
                            >
                                <Card.Body>
                                    <div className="experience-icon" aria-hidden="true">
                                        {experience.icon}
                                    </div>
                                    <h3 className="experience-title">{experience.title}</h3>
                                    <p className="experience-description">{experience.description}</p>
                                    <div className="experience-chips">
                                        <Badge bg="dark" className="experience-vibe" key={experience.vibe}>
                                            {vibeLabels[experience.vibe] || 'Cinema'}
                                        </Badge>
                                        {experience.highlights.map((highlight) => (
                                            <span className="experience-highlight" key={highlight}>
                                                {highlight}
                                            </span>
                                        ))}
                                    </div>
                                </Card.Body>
                            </Card>
                        </Col>
                    ))}
                </Row>
            </Container>
        </section>
    );
}

export default CinematicExperiences;
