const roadmapDiv = document.getElementById('roadmap');
const postsDiv = document.getElementById('posts');
const roadmapMeta = document.getElementById('roadmapMeta');
const statusEl = document.getElementById('formStatus');

const API_BASE = 'http://localhost:8080';
const sampleJD = `주요업무\n- Java/Spring 기반 백엔드 신규 개발 및 성능 개선\n- AWS 환경에서 서비스 운영 경험 보유자 우대\n자격요건\n- OOP 기반 개발 경험\n- MySQL, Redis, Kafka 경험 우대`;

document.getElementById('fillSample').addEventListener('click', () => {
    document.getElementById('jdText').value = sampleJD;
});

document.getElementById('generate').addEventListener('click', async () => {
    const payload = {
        jdText: document.getElementById('jdText').value.trim() || sampleJD,
        durationMonths: Number(document.getElementById('duration').value),
        dailyHours: Number(document.getElementById('hours').value),
        level: document.getElementById('level').value
    };

    statusEl.textContent = '로드맵 생성 중...';
    setLoading(roadmapDiv, true);

    try {
        const res = await fetch(`${API_BASE}/api/roadmap/from-jd`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (!res.ok) throw new Error('로드맵 생성에 실패했습니다. 백엔드 로그를 확인해주세요.');
        const data = await res.json();
        renderRoadmap(data);
        statusEl.textContent = '완료! 아래 로드맵을 확인하세요.';
    } catch (e) {
        statusEl.textContent = e.message;
        roadmapDiv.innerHTML = '<p class="empty">로드맵 생성에 실패했습니다. 서버 실행 여부를 확인하세요.</p>';
    } finally {
        setLoading(roadmapDiv, false);
    }
});

document.getElementById('fetchPosts').addEventListener('click', async () => {
    setLoading(postsDiv, true);
    try {
        const res = await fetch(`${API_BASE}/api/crawl/daily`);
        if (!res.ok) throw new Error('크롤링 데이터를 불러오지 못했습니다.');
        const data = await res.json();
        renderPosts(data);
    } catch (e) {
        postsDiv.innerHTML = `<p class="empty">${e.message}</p>`;
    } finally {
        setLoading(postsDiv, false);
    }
});

function setLoading(el, isLoading) {
    el.classList.toggle('loading', isLoading);
    if (isLoading) {
        el.innerHTML = '<p class="empty">잠시만 기다려주세요...</p>';
    }
}

function renderRoadmap(data) {
    roadmapDiv.classList.remove('empty');
    roadmapMeta.innerHTML = `총 ${data.totalWeeks}주 · 하루 ${data.dailyHours}시간 권장`;

    const keywordBadges = (data.keywords || []).map(k => `<span class="badge">${k}</span>`).join('');
    const stepBlocks = data.steps.map(step => {
        const weeks = step.weeks.map(week => `
            <div class="card week">
                <h4>${week.weekNumber}주차</h4>
                <div class="badges">${week.topics.map(t => `<span class="badge">${t}</span>`).join('')}</div>
                <p>${week.mission}</p>
            </div>
        `).join('');
        return `
            <div class="card">
                <h3>${step.title} (${step.estimatedWeeks}주)</h3>
                ${weeks}
            </div>
        `;
    }).join('');

    roadmapDiv.innerHTML = `
        <div class="card">
            <p class="eyebrow">추출 키워드</p>
            <div class="badges">${keywordBadges}</div>
        </div>
        ${stepBlocks}
    `;
}

function renderPosts(posts) {
    postsDiv.classList.remove('empty');
    if (!posts.length) {
        postsDiv.innerHTML = '<p class="empty">크롤링된 공고가 없습니다.</p>';
        return;
    }
    postsDiv.innerHTML = posts.map(post => `
        <div class="card">
            <p class="eyebrow">${post.company}</p>
            <h3>${post.role}</h3>
            <p>${post.location} · 마감 ${post.deadline}</p>
            <div class="badges">${post.keywords.map(k => `<span class="badge">${k}</span>`).join('')}</div>
            <a href="${post.url}" target="_blank" rel="noreferrer">공고 보기 →</a>
        </div>
    `).join('');
}
